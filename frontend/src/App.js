import { useState } from 'react';
import './App.css';
import NavBar from './components/NavBar';
import Products from './components/Products';
import axios from 'axios';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import PurcheasedCarts from './components/PurcheasedCarts';
import Alert from './components/Alert';
import OngoingCart from './components/OngoingCart';

function App() {
  const [selectedClient, setSelectedClient] = useState('');
  const [selectedDate, setSelectedDate] = useState('');
  const [message, setMessage] = useState(null);

  const handleClientSelect = (selectedClientId) => {
    console.log('Selected client ID:', selectedClientId);
    setSelectedClient(selectedClientId);
  };

  const handleDateSelect = (date) => {
    console.log('Selected date:', date);
    setSelectedDate(date);
  };

  const handleProductSelect = (productId) => {
    const newCart = {
      userId: selectedClient,
      creationDate: selectedDate,
      productId: productId,
      quantity: 1,
    };

    axios
      .post(process.env.REACT_APP_BASE_URL+'/shopping-carts', newCart)
      .then((response) => {
        console.log('Product added to cart:', response.data);
        setMessage('Se agrego un producto al carrito!');
      })
      .catch((error) => {
        console.error('Error creating shopping cart:', error);
        setMessage(error.response.data);
      });
  };

  const closeAlert = () => {
    setMessage(null);
  };

  return (
    <div className="App">
      <Router>
        <NavBar onClientSelect={handleClientSelect} onDateSelect={handleDateSelect} />
        <Routes>
          <Route path="/" element={<Products onProductSelect={handleProductSelect} />} />
          <Route path="/shopping-cart" element={<OngoingCart client={selectedClient} />} />
          <Route path="/purchased-carts" element={<PurcheasedCarts client={selectedClient} />} />
        </Routes>
      </Router>
      {message && <Alert message={message} onClose={closeAlert} />}
        <footer style={{position: 'fixed', bottom: '10px', left: '50%', transform: 'translateX(-50%)'}}>
        andyholes - 2023 - <a target="blank" href={process.env.REACT_APP_BASE_URL+"/swagger-ui/index.html"}>documentación</a></footer>
    </div>
  );
}

export default App;
