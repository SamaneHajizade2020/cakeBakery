//imrc
import React, { Component } from 'react';
import {Button, ButtonGroup, Container, Table} from 'reactstrap';
import AppNavbar from './AppNavbar';
import {Link} from 'react-router-dom';

//cc
class  InvetoriList extends Component {
 constructor(props){
     super(props);
     this.state = {inventories: []};
     this.remove = this.remove.bind(this);
  
 }
componentDidMount(){
    
    fetch('/inventory')
    .then(Response => Response.json())
    .then(data => this.setState({inventories: data }));
}

async remove(id){
    await fetch('/inventory/delete/${id}',
    {
        method : 'DELETE',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json' 
        }
    }).then(() => {
        let updateInventories = 
        [...this.state.inventories].filter(i => i.id !== id);
        this.setState({ inventories : updateInventories});
    });

}

    render() { 
    const{inventories} = this.state;

    const inventoriList = inventories.map(inventory => {
     // const name = `${inventory.name || ''}'; 
    const quantity =  `${inventory.quantity || ''}`;
        
    return <tr key={inventory.id}>
      <td style={{whiteSpace: 'nowrap'}}>{inventory.name}</td>
      <td>{quantity}</td>
      <td>{inventory.quantity}</td>
      <td>
        <ButtonGroup>
          <Button size="sm" color="primary" tag={Link} to={"/inventory/" + inventory.id}>Edit</Button>
          <Button size="sm" color="danger" onClick={() => this.remove(inventory.id)}>Delete</Button>
        </ButtonGroup>
      </td>
    </tr>
  });



    return (
      <div>
        <AppNavbar/>
        <Container fluid>
          <div className="float-right">
            <Button color="success" tag={Link} to="/inventory/fill">Add Inventory</Button>
          </div>
          <h3>My JUG Tour</h3>
          <Table className="mt-4">
            <thead>
            <tr>
              <th width="20%">Name</th>
              <th width="20%">Location</th>
              <th>Events</th>
              <th width="10%">Actions</th>
            </tr>
            </thead>
            <tbody>
            {inventoriList}
            </tbody>
          </Table>
        </Container>
      </div>
    );
  }
}

 
export default InvetoriList;