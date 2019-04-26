import React, { Component } from "react";
import Cell from "./Cell";
import PropTypes from "prop-types";

export default class Row extends Component {
  renderCells() {
    const cells = [];
    this.props.row.forEach((cell, i) => {
      cells.push(
        <Cell
          key={i}
          cell={cell}
          x={this.props.x}
          y={i}
          cellSize={this.props.cellSize}
          onClick={this.props.onClick}
          onRightClick={this.props.onRightClick}
          // onDoubleClick={this.props.onDoubleClick}
        />
      );
    });
    return cells;
  }

  render() {
    return (
      <div>
        {this.renderCells()}
      </div>
    );
  }
}

Row.propTypes = {
  x: PropTypes.number,
  row: PropTypes.arrayOf(PropTypes.object),
  board: PropTypes.arrayOf(PropTypes.object),
  onClick: PropTypes.func,
  onRightClick: PropTypes.func,
  cell: PropTypes.object,
  cellSize: PropTypes.number
};