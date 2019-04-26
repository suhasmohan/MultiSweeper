import React, { Component } from "react";
import Row from "./Row";
import "../styles/Board.css";
import PropTypes from "prop-types";

export default class Board extends Component {
  renderRows() {
    const rows = [];
    this.props.board.forEach((row, i) => {
      rows.push(
        <Row
          key={i}
          row={row}
          x={i}
          cellSize={this.props.cellSize}
          onClick={this.props.onClick}
          onRightClick={this.props.onRightClick}
          // onDoubleClick={this.props.onDoubleClick}
        />
      );
    });
    return rows;
  }

  render() {
    return (
      <div className="board">
        {this.renderRows()}
      </div>
    );
  }
}


Board.propTypes = {
  board: PropTypes.any,
  onClick: PropTypes.func,
  onRightClick: PropTypes.func,
  cell: PropTypes.object,
  cellSize: PropTypes.number
};

