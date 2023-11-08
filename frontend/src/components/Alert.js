import React, { useEffect } from 'react';

function Alert({ message, onClose }) {
  useEffect(() => {
    const timeout = setTimeout(() => {
      onClose();
    }, 3000);

    return () => {
      clearTimeout(timeout);
    };
  }, [onClose]);

  const alertStyle = {
    position: 'fixed',
    bottom: '10px',
    left: '50%',
    transform: 'translateX(-50%)',
    zIndex: 9999,
    fontSize:'150%',
    width:'50vw'
  };

  return (
    <div style={alertStyle}>
      <div className="alert alert-danger" role="alert">
      {message}
      </div>
    </div>
  );
}

export default Alert;
