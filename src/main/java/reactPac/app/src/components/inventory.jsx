import React, { Component } from 'react';

class Inventory extends Component{
	render() {
		return (
			<tr>
				<td>{this.props.inventory.name}</td>
				<td>{this.props.inventory.quantity}</td>
			</tr>
		)
	}
}

export default Inventory;