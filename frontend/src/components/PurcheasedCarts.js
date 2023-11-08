import { useEffect, useState } from "react";
import axios from 'axios';
import Badge from 'react-bootstrap/Badge';
import ListGroup from 'react-bootstrap/ListGroup';

function PurcheasedCarts({ client }) {
  const [purchasedCarts, setPurcheasedCarts] = useState([]);
  const [monthPayments, setMonthPayments] = useState(null);

  useEffect(() => {
    axios.get(process.env.REACT_APP_BASE_URL+'/shopping-carts?paid=true&user-id=' + client)
      .then((response) => {
        setPurcheasedCarts(response.data.reverse());
      })
      .catch((error) => {
        console.error('Error fetching product data:', error);
      });
  }, [client]);

  useEffect(()=>{
    axios.get(process.env.REACT_APP_BASE_URL+'/clients/'+client+'/current-month-payments')
    .then((response) => {
      setMonthPayments(response.data);
    })
    .catch((error) => {
      console.error('Error fetching product data:', error);
    });
}, [client]);

  if (client === null || client === '') {
    return <h1>Por favor, identifíquese como cliente para continuar</h1>
  } else if (purchasedCarts.length === 0) {
    return (
      <h1>
        Parece que aún no tienes ninguna compra!<br/>Cuando pagues por un carrito lo verás aquí
      </h1>
    );
  } else {
    return (
      <div className="d-flex justify-content-center align-items-center h-100 flex-column-reverse">
        <div style={{width:'35em'}}>
          {purchasedCarts.map((cart) => (
            <div key={cart.id} className="cart-item mb-5">
              <ListGroup as="ol">
                <ListGroup.Item as="li" variant="dark">
                Carrito creado el {cart.creationDate[2]}-{cart.creationDate[1]}-{cart.creationDate[0]}
                {cart.createdOnSpecialDate ? (<span> <Badge bg="dark" className='text p-2 ms-2'>Fecha especial! 😃</Badge></span>) 
                : (<span></span>) }
                </ListGroup.Item>
                {cart.products.sort((a,b)=>a.product.id - b.product.id).map((product) => (
                  <div key={product.product.id}>
                  <ListGroup.Item
                    as="li"
                    className="d-flex justify-content-between align-items-start"
                  >
                    <img src={"data:image/octet-stream;base64," + product.product.image}
                      style={{ height: '50px', width: '50px', objectFit: 'contain' }} 
                      alt={product.product.name}/>
                    <div>
                      <div className="fw-bold">{product.product.name}</div>
                      {product.quantity} x $ {product.product.price.toLocaleString('de-DE')}
                    </div>
                    <h6>{'Sub-Total: '}
                      <Badge bg="dark" className='text'>
                        $ {(product.quantity * product.product.price).toLocaleString('de-DE')}
                      </Badge>
                    </h6>
                  </ListGroup.Item>
                  </div>
                ))}
                <ListGroup.Item as="li" variant="flush" style={{ color: 'white', backgroundColor: '#404040' }}>
                  Total pagado: $ {cart.totalPrice.toLocaleString('de-DE')}
                  
                  {cart.totalPrice !== cart.priceWithoutDiscounts ? (
                  <span> <Badge bg="danger" className='text p-2 ms-2'>{(((cart.priceWithoutDiscounts-cart.totalPrice) /cart.totalPrice)*100).toFixed(0)}% OFF</Badge></span>) 
                  : (<span></span>)}
                  {cart.paidByVipUser ? (<span> <Badge bg="dark" className='text p-2 ms-2'>Vip ⭐</Badge></span>) 
                  : (<span></span>) }
                </ListGroup.Item>
              </ListGroup>
            </div>
          ))}
        </div>
        <h4>Total gastado en este mes: $ {monthPayments.toLocaleString('de-DE')}</h4>
      </div>
    );
  }
}

export default PurcheasedCarts;