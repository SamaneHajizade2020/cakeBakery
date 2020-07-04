import React, {Component } from 'react';
import Inventory from './inventory';


class InventoryList extends React.Component{
	render() {

		const inventory =
			this.props.inventory.map(inventory =>
			<Inventory key={inventory.id}
					  inventory={inventory}/>
		);
		return (
			<table>
				<tbody>
					<tr>
						<th>  Name </th>
						<th>quantity</th>
					</tr>
					{inventory}
				</tbody>
			</table>
		)
	}
}
export default InventoryList;