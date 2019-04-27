import React, { Component } from "react";
import { connect } from "react-redux";
import Bomb from "react-icons/lib/fa/certificate";
import Board from "./Board";
// import config from "../config";

import "../styles/Game.css";
import PropTypes from "prop-types";

class Game extends Component {
  constructor(props) {
    super(props);
    this.state = { board: undefined };
    this._updateBoard();

    this.handleClick = this.handleClick.bind(this);
    this.handleClickCell = this.handleClickCell.bind(this);
    this.handleRightClickCell = this.handleRightClickCell.bind(this);
  }

  _updateBoard() {
    this.updateInterval = setInterval(() => {
      fetch("/api/getBoard", {
        method: "GET"
      })
        .then(res => res.json())
        .catch(err => console.error(err))
        .then(res => {
          // console.log(res);
          this.setState({
            status: res.status,
            nRows: res.nRows,
            nCols: res.nCols,
            bomb: res.bomb,
            cellSize: Math.floor(1600 / (res.nCols + 40) + 10),
            board: res.cells
          });
        });
    }, 300);
  }

  handleClick(e) {
    e.preventDefault();
    fetch("/api/click", {
      method: "POST",
      body: JSON.stringify({ row: 0, col: 0, type: "restart" })
    });
  }

  handleClickCell(x, y) {
    const status = this.state.status;
    if (status !== "PLAY") {
      return;
    }
    // TODO Call API: {Click: open}
    // this._open(x, y);
    fetch("/api/click", {
      method: "POST",
      body: JSON.stringify({ row: x, col: y, type: "open" })
    });
  }

  handleRightClickCell(x, y) {
    const status = this.state.status;
    if (status !== "PLAY") {
      return;
    }
    fetch("/api/click", {
      method: "POST",
      body: JSON.stringify({ row: x, col: y, type: "flag" })
    });
  }

  render() {
    const { status, nCols, bomb, cellSize, board } = this.state;
    const boardWidth = nCols;

    const boardWidthPx = boardWidth * cellSize || 320;
    let statusElement = <span className="status" />;
    if (status === "LOSE") {
      statusElement = (
        <span id="gameover" className="status">
          LOSE
        </span>
      );
    } else if (status === "WIN") {
      statusElement = (
        <span id="clear" className="status">
          WIN!
        </span>
      );
    }
    return (
      <div id="game" style={{ width: boardWidthPx }}>
        <h1>Minesweeper</h1>
        <div id="menu">
          <button onClick={this.handleClick} id="restart">
            Restart
          </button>
          {/*<select value={difficulty} onChange={(e) => this.changeDifficulty(e)} style={{ marginRight: 5 }}>
            <option value={"easy"} key={"easy"}>Easy</option>
            <option value={"normal"} key={"normal"}>Normal</option>
            <option value={"hard"} key={"hard"}>Hard</option>
            <option value={"veryHard"} key={"veryHard"}>Very Hard</option>
            <option value={"maniac"} key={"maniac"}>Maniac</option>
          </select>*/}
          <span id="bomb">
            <Bomb style={{ marginTop: -3 }} /> {bomb}
          </span>
          {statusElement}
        </div>
        {board && (
          <Board
            board={board}
            cellSize={cellSize}
            onClick={this.handleClickCell}
            onRightClick={this.handleRightClickCell}
            onDoubleClick={this.handleDoubleClickCell}
          />
        )}

        <div>
          <p>
            <span style={{ fontWeight: "bold" }}>HOW TO PLAY</span>
            <br />
            <span style={{ fontSize: 14 }}>Click: Open a cell.</span>
            <br />
            <span style={{ fontSize: 14 }}>Right Click: Toggle a flag.</span>
            <br />
          </p>
          <hr />
          <p style={{ textAlign: "right" }}>
            <span>Created by </span>
            <a
              href="https://github.com/davidychen"
              target="_blank"
              rel="noopener noreferrer"
            >
              David
            </a>
            {", "}
            <a
              href="https://github.com/hoonskii"
              target="_blank"
              rel="noopener noreferrer"
            >
              Hoon
            </a>
            {", "}
            <a
              href="https://github.com/suhasmohan"
              target="_blank"
              rel="noopener noreferrer"
            >
              Suhas
            </a>
            {", and "}
            <a
              href="https://github.com/bhandarysushruth"
              target="_blank"
              rel="noopener noreferrer"
            >
              Sushrut
            </a>
            <br />
            <span>View </span>
            <a
              href="https://github.com/suhasmohan/MultiSweeper/tree/master"
              target="_blank"
              rel="noopener noreferrer"
            >
              Code
            </a>
          </p>
        </div>
      </div>
    );
  }
}

const mapStateToProps = state => state.game;

Game.propTypes = {
  x: PropTypes.number,
  y: PropTypes.number,
  onClick: PropTypes.func,
  onRightClick: PropTypes.func,
  cell: PropTypes.object,
  cellSize: PropTypes.number
};

export default connect(mapStateToProps)(Game);
