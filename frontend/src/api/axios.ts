import axios from 'axios'

export const TOKEN_KEY = 'url_shortener_token'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  headers: { 'Content-Type': 'application/json' }
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY)

  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }

  return config
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      const url: string = error.config?.url ?? ''
      const isAuthEndpoint = url.includes('/auth/login') || url.includes('/auth/register')

      if (!isAuthEndpoint) {
        localStorage.removeItem(TOKEN_KEY)
        if (window.location.pathname !== '/login') {
          window.location.assign('/login')
        }
      }
    }

    return Promise.reject(error)
  }
)

export default api
