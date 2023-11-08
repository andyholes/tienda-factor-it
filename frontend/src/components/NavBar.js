import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';

function NavBar({ onClientSelect, onDateSelect }) {
  const [clients, setClients] = useState([]);
  const [selectedClient, setSelectedClient] = useState('');
  const [selectedDate, setSelectedDate] = useState('');

  useEffect(() => {
    axios.get(process.env.REACT_APP_BASE_URL+'/clients')
      .then((response) => {
        setClients(response.data);
      })
      .catch((error) => {
        console.error('Error fetching client data:', error);
      });
  }, []);

  const handleSelectChange = (event) => {
    const selectedClientId = event.target.value;
    setSelectedClient(selectedClientId);
    onClientSelect(selectedClientId);
  };

  const handleDateChange = (event) => {
    const selectedDate = event.target.value;
    setSelectedDate(selectedDate);
    onDateSelect(selectedDate);
  };

  const linkStyle = {
    textDecoration: 'none',
    color: 'white',
    marginLeft: '1.5em'
  };

  return (
    <Navbar expand="xl" className="bg-body-tertiary" bg="dark" data-bs-theme="dark" style={{ marginBottom: '2em' }}>
      <Container>
        <Navbar.Brand className="me-4">
          <img
            src="https://www.factorit.com.ar/images/favicon.png"
            width="30"
            height="30"
            className="d-inline-block align-top me-2"
            alt="FACTOR IT logo"
          />
          Tienda FACTOR IT
        </Navbar.Brand>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="me-auto">
            <select value={selectedClient} onChange={handleSelectChange} className="me-4">
              <option value="">Seleccione un cliente</option>
              {clients.map((client) => (
                <option key={client.id} value={client.id}>
                  {client.name} {client.lastName} {client.vip ? ' ⭐' : ''}
                </option>
              ))}
            </select>
            <input type="date" defaultValue={new Date()} onChange={handleDateChange} />
          </Nav>
          <Nav>
            <Link to="/" style={linkStyle}>Listado de productos</Link>
            <Link to="/shopping-cart" style={linkStyle}>Ver carrito</Link>
            <Link to="/purchased-carts" style={linkStyle}>Historial de compras</Link>
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
}

export default NavBar;