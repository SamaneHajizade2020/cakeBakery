import React, { Component } from 'react';
//import React from 'react';
import logo from './logo.svg';
import './App.css';

class App extends Component {
  state = {
    isLoading: true,
   // groups: [],
    inventories: []
  };

  async componentDidMount() {
    const response = await fetch('/inventory');
    const body = await response.json();
   // this.setState({ groups: body});
   this.setState({ inventories: body, isLoading: false });
  }

  render() {
    //const {groups} = this.state;
    const {inventories, isLoading} = this.state;

  return (
    <div className="App">
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
  );
}
}

export default App;
