import React, { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import * as z from 'zod'
import { useMutation } from '@tanstack/react-query'
import { login } from '../api/auth'
import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth'
import { useToast } from '../components/Toast'
import { getApiErrorMessage } from '../utils'
import { Eye, EyeOff, KeyRound, LogIn, Mail } from 'lucide-react'

const schema = z.object({
  email: z.string().email('Enter a valid email address'),
  password: z.string().min(6, 'Password must be at least 6 characters')
})

type Form = z.infer<typeof schema>

const Login: React.FC = () => {
  const { register, handleSubmit, formState: { errors } } = useForm<Form>({ resolver: zodResolver(schema) })
  const { saveToken, token } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const { showToast } = useToast()
  const [showPassword, setShowPassword] = useState(false)

  const from = (location.state as { from?: string } | null)?.from ?? '/dashboard'

  const mutation = useMutation({
    mutationFn: (data: Form) => login(data),
    onSuccess: (data) => {
      saveToken(data.token)
      showToast('Welcome back', 'success')
      navigate(from, { replace: true })
    },
    onError: (err) => {
      showToast(getApiErrorMessage(err, 'Login failed. Check your credentials.'), 'error')
    }
  })

  if (token) {
    return <Navigate to="/dashboard" replace />
  }

  const fieldError = (name: keyof Form) => errors[name]?.message

  return (
    <div className="auth-wrap">
      <div className="auth-panel">
        <div className="auth-panel__head">
          <span className="auth-mark" />
          <h1 className="auth-panel__title">Welcome back</h1>
          <p className="auth-panel__sub">Sign in to manage your short links.</p>
        </div>

        <form onSubmit={handleSubmit((d) => mutation.mutate(d))} className="form" noValidate>
          <div className="form-row">
            <label htmlFor="email">Email</label>
            <div className="field-wrap">
              <Mail size={16} className="field-icon" />
              <input
                id="email"
                type="email"
                autoComplete="email"
                className={`field ${fieldError('email') ? 'field--error' : ''}`}
                placeholder="you@example.com"
                {...register('email')}
              />
            </div>
            {fieldError('email') && <p className="form-error">{fieldError('email')}</p>}
          </div>

          <div className="form-row">
            <label htmlFor="password">Password</label>
            <div className="field-wrap">
              <KeyRound size={16} className="field-icon" />
              <input
                id="password"
                type={showPassword ? 'text' : 'password'}
                autoComplete="current-password"
                className={`field field--pw ${fieldError('password') ? 'field--error' : ''}`}
                placeholder="••••••••"
                {...register('password')}
              />
              <button
                type="button"
                className="pw-toggle"
                onClick={() => setShowPassword((value) => !value)}
                aria-label={showPassword ? 'Hide password' : 'Show password'}
              >
                {showPassword ? <EyeOff size={16} /> : <Eye size={16} />}
              </button>
            </div>
            {fieldError('password') && <p className="form-error">{fieldError('password')}</p>}
          </div>

          <button type="submit" className="btn btn--primary btn--block" disabled={mutation.isPending}>
            {mutation.isPending ? 'Signing in...' : <><LogIn size={15} />Sign in</>}
          </button>
        </form>

        <p className="form-footer">
          Don&apos;t have an account?{' '}
          <Link to="/register">Create one</Link>
        </p>
      </div>
    </div>
  )
}

export default Login
