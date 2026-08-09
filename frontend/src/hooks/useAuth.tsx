import { createContext, useContext, useMemo, useState } from 'react'
import { TOKEN_KEY } from '../api/axios'

type AuthContextValue = {
  token: string | null
  saveToken: (token: string) => void
  clearToken: () => void
}

const AuthContext = createContext<AuthContextValue | null>(null)

export const AuthProvider: React.FC<React.PropsWithChildren> = ({ children }) => {
  const [token, setToken] = useState<string | null>(() => localStorage.getItem(TOKEN_KEY))

  const saveToken = (t: string) => {
    localStorage.setItem(TOKEN_KEY, t)
    setToken(t)
  }

  const clearToken = () => {
    localStorage.removeItem(TOKEN_KEY)
    setToken(null)
  }

  const value = useMemo(() => ({ token, saveToken, clearToken }), [token])

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export const useAuth = () => {
  const context = useContext(AuthContext)

  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider')
  }

  return context
}

export default useAuth
