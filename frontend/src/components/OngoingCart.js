import { useEffect, useState } from "react";
import axios from 'axios';
import Alert from './Alert';
import Badge from 'react-bootstrap/Badge';
import ListGroup from 'react-bootstrap/ListGroup';
import Button from 'react-bootstrap/Button';
import CloseButton from 'react-bootstrap/CloseButton';

function OngoingCart({ client: clientId }) {
  const [ongoingCart, setOngoingCart] = useState([]);
  const [message, setMessage] = useState(null);

  useEffect(() => {
    axios.get(process.env.REACT_APP_BASE_URL+'/shopping-carts?paid=false&user-id=' + clientId)
      .then((response) => {
        setOngoingCart(response.data);
      })
      .catch((error) => {
        console.error('Error fetching product data:', error);
      });
  }, [clientId]);

  async function handleItemDeletion(productId) {
    try {
      await axios.delete(process.env.REACT_APP_BASE_URL+'/shopping-carts/' + clientId + '/' + productId)
        .then((response) => {
          console.log(response.data)
          setMessage(response.data);
        })
        .catch((error) => {
          console.error('Error fetching product data:', error);
        });
      const response = await axios.get(process.env.REACT_APP_BASE_URL+'/shopping-carts?paid=false&user-id=' + clientId);
      setOngoingCart(response.data);
    } catch (error) {
      console.error('Error deleting product:', error);
    }
  }

  function handleCartDeletion() {
    axios.delete(process.env.REACT_APP_BASE_URL+'/shopping-carts/' + ongoingCart[0].id)
      .then((response) => {
        setMessage('Carrito eliminado correctamente!');
        console.log(response.data)
      })
      .catch((error) => {
        console.error('Error fetching product data:', error);
      });
    setOngoingCart([]);
  }

  function handlePayment() {
    axios.put(process.env.REACT_APP_BASE_URL+'/shopping-carts/' + ongoingCart[0].id)
      .then((response) => {
        setMessage(response.data);
      })
      .catch((error) => {
        console.error('Error fetching product data:', error);
      });
    setOngoingCart([]);
  }

  async function handleUnits(e, productId) {
    const date = ongoingCart[0].creationDate;

    const updatedProduct = {
      userId: parseInt(clientId),
      creationDate: date,
      productId: productId,
      quantity: parseInt(e.target.value)
    }

    try {
      await axios.post(process.env.REACT_APP_BASE_URL+'/shopping-carts', updatedProduct)
        .catch((error) => {
          console.error('Error fetching product data:', error);
        });
      const response = await axios.get(process.env.REACT_APP_BASE_URL+'/shopping-carts?paid=false&user-id=' + clientId);
      setOngoingCart(response.data);
    } catch (error) {
      console.error('Error deleting product:', error);
    }
  }

  const inputStyles = {
    width: '3em',
    height: '1.5em'
  };

  const closeAlert = () => {
    setMessage(null);
  };

  if (clientId === null || clientId === '') {
    return <h1>Por favor, identifíquese como cliente para continuar</h1>
  } else if (ongoingCart.length === 0) {
    return (
      <>
        <h1>
          ¡Ups! Parece que no tienes un carrito activo<br />Comienza agregando algunos productos
        </h1>
        {message && <Alert message={message} onClose={closeAlert} />}
      </>
    );
  } else {
    return (
      <div className="d-flex justify-content-center align-items-center h-100">
        <div className="column-layout" style={{ width: '35em' }}>
          {ongoingCart.map((cart) => (
            <div key={cart.id} className="cart-item mb-5">
              <ListGroup as="ol">
                <ListGroup.Item as="li" variant="dark">
                  Carrito creado el {cart.creationDate[2]}-{cart.creationDate[1]}-{cart.creationDate[0]}
                  {cart.createdOnSpecialDate ? (<span> <Badge bg="dark" className='text p-2 ms-2'>Fecha especial! 😃</Badge></span>)
                    : (<span></span>)}
                </ListGroup.Item>
                {cart.products.sort((a, b) => a.product.id - b.product.id).map((product) => (
                  <div key={product.product.id}>
                    <ListGroup.Item
                      as="li"
                      className="d-flex justify-content-between align-items-start"
                    >
                      <img src={"data:image/octet-stream;base64," + product.product.image}
                        style={{ height: '50px', width: '50px', objectFit: 'contain' }}
                        alt={product.product.name} />
                      <div>
                        <div className="fw-bold">{product.product.name}</div>
                        <input type="number" min={1}
                          onChange={e => handleUnits(e, product.product.id)}
                          defaultValue={product.quantity}
                          style={inputStyles} /> x $ {product.product.price.toLocaleString('de-DE')}
                      </div>
                      <h6>{'Sub-Total: '}
                        <Badge bg="dark" className='text'>
                          $ {(product.quantity * product.product.price).toLocaleString('de-DE')}
                        </Badge>
                      </h6>
                      <CloseButton onClick={() => handleItemDeletion(product.product.id)} />
                    </ListGroup.Item>
                  </div>
                ))}
                <ListGroup.Item as="li" variant="flush" style={{ color: 'white', backgroundColor: '#404040' }}>
                  {cart.totalPrice === cart.priceWithoutDiscounts ? (
                    <span>
                      Total: $ {cart.priceWithoutDiscounts.toLocaleString('de-DE')}
                    </span>
                  ) : (
                    <span>
                      Total: <del style={{ color: '#FF3333' }}> $ {cart.priceWithoutDiscounts.toLocaleString('de-DE')} </del>
                      <span> $ {cart.totalPrice.toLocaleString('de-DE')}</span>
                    </span>
                  )}
                </ListGroup.Item>
              </ListGroup>
              <div style={{ marginTop: '2%' }}>
                <Button variant="success" size="lg" style={{ width: '60%', marginRight: '3%' }}
                  onClick={() => handlePayment()}
                >
                  Comprar carrito
                </Button>
                <Button variant="danger" size="lg" style={{ width: '37%' }}
                  onClick={() => handleCartDeletion()}
                >
                  Eliminar
                </Button>
              </div>
            </div>
          ))}
        </div>
        {message && <Alert message={message} onClose={closeAlert} />}
      </div>
    );
  }
}

export default OngoingCart;