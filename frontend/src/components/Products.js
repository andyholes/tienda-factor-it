import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Button from 'react-bootstrap/Button';
import Card from 'react-bootstrap/Card';

function Products({onProductSelect}) {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState([]);

    useEffect(() => {
      axios.get(process.env.REACT_APP_BASE_URL+'/products')
        .then((response) => {
          setLoading(false)
          setProducts(response.data);
        })
        .catch((error) => {
          console.error('Error fetching product data:', error);
        });
    }, []);

    const handleProductSelect = (productId) => {        
        onProductSelect(productId);
      };

  return (
    <>
      <ul>
        {products.map((product) => (
          <li key={product.id} className="d-inline-flex">
          <Card className="d-inline-flex p-4 m-2" style={{width:'20em'}}>
            <Card.Img
              variant="top"
              src={"data:image/octet-stream;base64," + product.image}
              style={{height: '15em', objectFit:'contain'}}
              />
            <Card.Body>
              <Card.Title>{product.name}</Card.Title>
              <Card.Text>
                {"$" + product.price.toLocaleString('de-DE')}
              </Card.Text>
              <Button
                variant="dark"
                onClick={() => handleProductSelect(product.id)}
              >
                Agregar al carrito
              </Button>
            </Card.Body>
          </Card>
          </li>
        ))}
      </ul>
      {loading && <img src="https://i.gifer.com/ZKZg.gif" 
      style={{height:'7em', position:'absolute', top:'45%'}}/>}
    </>
  );
}

export default Products;
