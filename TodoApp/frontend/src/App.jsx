import React, { useState, useEffect } from 'react'
import { 
  GeistProvider, 
  CssBaseline, 
  Page, 
  Text, 
  Input, 
  Button, 
  Card, 
  Spacer, 
  Checkbox,
  Loading,
  Note,
  Grid,
  Divider,
  Badge
} from '@geist-ui/react'

const API_BASE_URL = '/api'

function App() {
  const [todos, setTodos] = useState([])
  const [newTodo, setNewTodo] = useState({ title: '', description: '' })
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  // コンポーネントマウント時にTodoリストを取得
  useEffect(() => {
    fetchTodos()
  }, [])

  // 全てのTodoを取得
  const fetchTodos = async () => {
    try {
      setLoading(true)
      const response = await fetch(`${API_BASE_URL}/todos`)
      if (!response.ok) {
        throw new Error('Todoの取得に失敗しました')
      }
      const data = await response.json()
      setTodos(data)
      setError('')
    } catch (err) {
      setError(err.message)
      console.error('Todo取得エラー:', err)
    } finally {
      setLoading(false)
    }
  }

  // 新しいTodoを作成
  const createTodo = async (e) => {
    e.preventDefault()
    
    if (!newTodo.title.trim()) {
      setError('タイトルを入力してください')
      return
    }

    setSubmitting(true)
    try {
      const response = await fetch(`${API_BASE_URL}/todos`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(newTodo)
      })

      if (!response.ok) {
        throw new Error('Todoの作成に失敗しました')
      }

      const createdTodo = await response.json()
      setTodos([createdTodo, ...todos])
      setNewTodo({ title: '', description: '' })
      setError('')
    } catch (err) {
      setError(err.message)
      console.error('Todo作成エラー:', err)
    } finally {
      setSubmitting(false)
    }
  }

  // Todoの完了状態を切り替え
  const toggleTodo = async (todo) => {
    try {
      const updatedTodo = { ...todo, completed: !todo.completed }
      
      const response = await fetch(`${API_BASE_URL}/todos/${todo.id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(updatedTodo)
      })

      if (!response.ok) {
        throw new Error('Todoの更新に失敗しました')
      }

      const updated = await response.json()
      setTodos(todos.map(t => t.id === todo.id ? updated : t))
      setError('')
    } catch (err) {
      setError(err.message)
      console.error('Todo更新エラー:', err)
    }
  }

  // Todoを削除
  const deleteTodo = async (id) => {
    if (!window.confirm('このTodoを削除しますか？')) {
      return
    }

    try {
      const response = await fetch(`${API_BASE_URL}/todos/${id}`, {
        method: 'DELETE'
      })

      if (!response.ok) {
        throw new Error('Todoの削除に失敗しました')
      }

      setTodos(todos.filter(todo => todo.id !== id))
      setError('')
    } catch (err) {
      setError(err.message)
      console.error('Todo削除エラー:', err)
    }
  }

  return (
    <GeistProvider>
      <CssBaseline />
      <Page>
        <Page.Header>
          <Text h1 style={{ marginBottom: 0, fontWeight: 300, letterSpacing: '-0.02em' }}>
            Todo
          </Text>
          <Text p type="secondary" style={{ marginTop: '8px', fontSize: '14px' }}>
            Simple task management with Java + MySQL + React
          </Text>
        </Page.Header>

        <Page.Content>
          {error && (
            <Note type="error" filled={false} style={{ marginBottom: '24px' }}>
              {error}
            </Note>
          )}

          {/* 新しいTodo作成フォーム */}
          <Card style={{ marginBottom: '32px' }}>
            <Text h4 style={{ marginTop: 0, fontWeight: 400 }}>Add new task</Text>
            <form onSubmit={createTodo}>
              <Grid.Container gap={1} style={{ marginBottom: '16px' }}>
                <Grid xs={24} md={12}>
                  <Input
                    placeholder="Task title..."
                    value={newTodo.title}
                    onChange={(e) => setNewTodo({ ...newTodo, title: e.target.value })}
                    width="100%"
                    clearable
                  />
                </Grid>
                <Grid xs={24} md={12}>
                  <Input
                    placeholder="Description (optional)"
                    value={newTodo.description}
                    onChange={(e) => setNewTodo({ ...newTodo, description: e.target.value })}
                    width="100%"
                    clearable
                  />
                </Grid>
              </Grid.Container>
              <Button 
                type="secondary" 
                htmlType="submit" 
                loading={submitting}
                auto
              >
                + Add Task
              </Button>
            </form>
          </Card>

          {/* Todoリスト */}
          {loading ? (
            <div style={{ textAlign: 'center', padding: '48px 0' }}>
              <Loading>Loading tasks...</Loading>
            </div>
          ) : (
            <>
              {todos.length === 0 ? (
                <Card style={{ textAlign: 'center', padding: '48px 24px' }}>
                  <Text type="secondary">
                    No tasks yet. Create your first task above.
                  </Text>
                </Card>
              ) : (
                <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                  {todos.map((todo, index) => (
                    <Card 
                      key={todo.id} 
                      style={{ 
                        opacity: todo.completed ? 0.6 : 1,
                        transition: 'opacity 0.2s ease'
                      }}
                      hoverable
                    >
                      <div style={{ 
                        display: 'flex', 
                        alignItems: 'flex-start', 
                        gap: '16px' 
                      }}>
                        <Checkbox
                          checked={todo.completed}
                          onChange={() => toggleTodo(todo)}
                          style={{ marginTop: '2px' }}
                        />
                        <div style={{ flex: 1, minWidth: 0 }}>
                          <Text 
                            style={{ 
                              textDecoration: todo.completed ? 'line-through' : 'none',
                              fontWeight: 500,
                              marginBottom: todo.description ? '4px' : 0
                            }}
                          >
                            {todo.title}
                          </Text>
                          {todo.description && (
                            <Text 
                              type="secondary" 
                              style={{ 
                                fontSize: '14px',
                                textDecoration: todo.completed ? 'line-through' : 'none'
                              }}
                            >
                              {todo.description}
                            </Text>
                          )}
                        </div>
                        <div style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
                          <Badge 
                            type={todo.completed ? 'success' : 'secondary'} 
                            style={{ fontSize: '12px' }}
                          >
                            {todo.completed ? 'Done' : 'Todo'}
                          </Badge>
                          <Button
                            auto
                            scale={0.8}
                            type="error"
                            ghost
                            onClick={() => deleteTodo(todo.id)}
                          >
                            ×
                          </Button>
                        </div>
                      </div>
                    </Card>
                  ))}
                </div>
              )}
            </>
          )}

          <Spacer h={2} />
          <Divider />
          <Spacer h={1} />
          
          {/* 技術スタック情報 */}
          <div style={{ textAlign: 'center' }}>
            <Text type="secondary" style={{ fontSize: '13px' }}>
              Built with <strong>Java</strong> + <strong>MySQL</strong> + <strong>React 19</strong> + <strong>Geist UI</strong>
            </Text>
          </div>
        </Page.Content>
      </Page>
    </GeistProvider>
  )
}

export default App
