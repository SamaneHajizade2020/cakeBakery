import React, { Component } from 'react';
//import React from 'react';
import logo from './logo.svg';
import './App.css';
import InventoryList from './components/inventorylist';

class App extends Component {
  state = {
    inventory: []
  };

  async componentDidMount() {
    const response = await fetch('/inventory');
    const body = await response.json();
   this.setState({ inventory: body});
  }

  render() {
    const {inventory} = this.state;
        return (
                <InventoryList
                            inventory={this.state.inventory}/>
            );


    /*<div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />

        <div className="App-intro">
            <h2>Inventory List</h2>
            {inventories.map(inventory =>
              <div key={inventory.id}>
                {inventory.name}
                {inventory.quantity}
              </div>
            )}
          </div>
      </header>
    </div>
  );*/
}
}

export default App;
