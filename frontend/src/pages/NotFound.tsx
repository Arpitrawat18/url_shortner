import React from 'react'
import { Link } from 'react-router-dom'
import { ArrowLeft, Compass } from 'lucide-react'

const NotFound: React.FC = () => (
  <div className="notfound">
    <span className="empty__icon" style={{ width: 56, height: 56 }}>
      <Compass size={26} />
    </span>
    <p className="notfound__code">
      ERR<span className="slash">/</span>404
    </p>
    <h1 className="notfound__title">Page not found</h1>
    <p className="notfound__sub">
      The page you&apos;re looking for doesn&apos;t exist or has been moved. Return to the index and continue your work.
    </p>
    <div className="notfound__action">
      <Link to="/" className="btn btn--primary">
        <ArrowLeft size={15} />
        Back to index
      </Link>
    </div>
  </div>
)

export default NotFound
