import React, { Component } from 'react';

class recipe extends Component{
	render() {
		return (
			<tr>
				<td>{this.props.recipe.name}</td>
				<td>{this.props.recipe.instructions}</td>
				<td>{this.props.recipe.ingredients}</td>
			</tr>
		)
	}
}

export default recipe;