import React, { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import * as z from 'zod'
import { useMutation } from '@tanstack/react-query'
import { register as apiRegister } from '../api/auth'
import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth'
import { useToast } from '../components/Toast'
import { getApiErrorMessage } from '../utils'
import { Eye, EyeOff, KeyRound, Mail, UserPlus, User as UserIcon } from 'lucide-react'

const schema = z.object({
  name: z.string().min(2, 'Name must be at least 2 characters'),
  email: z.string().email('Enter a valid email address'),
  password: z.string().min(6, 'Password must be at least 6 characters')
})

type Form = z.infer<typeof schema>

const Register: React.FC = () => {
  const { register, handleSubmit, formState: { errors } } = useForm<Form>({ resolver: zodResolver(schema) })
  const { saveToken, token } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const { showToast } = useToast()
  const [showPassword, setShowPassword] = useState(false)

  const from = (location.state as { from?: string } | null)?.from ?? '/dashboard'

  const mutation = useMutation({
    mutationFn: (data: Form) => apiRegister(data),
    onSuccess: (data) => {
      saveToken(data.token)
      showToast('Account created', 'success')
      navigate(from, { replace: true })
    },
    onError: (err) => {
      showToast(getApiErrorMessage(err, 'Registration failed. Try again.'), 'error')
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
          <h1 className="auth-panel__title">Create your account</h1>
          <p className="auth-panel__sub">Start shortening links in seconds.</p>
        </div>

        <form onSubmit={handleSubmit((d) => mutation.mutate(d))} className="form" noValidate>
          <div className="form-row">
            <label htmlFor="name">Name</label>
            <div className="field-wrap">
              <UserIcon size={16} className="field-icon" />
              <input
                id="name"
                type="text"
                autoComplete="name"
                className={`field ${fieldError('name') ? 'field--error' : ''}`}
                placeholder="Jane Doe"
                {...register('name')}
              />
            </div>
            {fieldError('name') && <p className="form-error">{fieldError('name')}</p>}
          </div>

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
                autoComplete="new-password"
                className={`field field--pw ${fieldError('password') ? 'field--error' : ''}`}
                placeholder="At least 6 characters"
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
            {mutation.isPending ? 'Creating account...' : <><UserPlus size={15} />Create account</>}
          </button>
        </form>

        <p className="form-footer">
          Already have an account?{' '}
          <Link to="/login">Sign in</Link>
        </p>
      </div>
    </div>
  )
}

export default Register
