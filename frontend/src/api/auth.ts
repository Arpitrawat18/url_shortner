import api from './axios'
import { LoginRequest, RegisterRequest, AuthResponse } from '../types'

export const login = async (payload: LoginRequest): Promise<AuthResponse> => {
  const { data } = await api.post('/api/v1/auth/login', payload)
  return data
}

export const register = async (payload: RegisterRequest): Promise<AuthResponse> => {
  const { data } = await api.post('/api/v1/auth/register', payload)
  return data
}
