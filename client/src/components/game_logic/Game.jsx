import React, { useState, useEffect } from 'react';
import { Client } from '@stomp/stompjs';
function Game() {
    const [connected, setConnected] = useState(false);
    const [stompClient, setStompClient] = useState(null);
    const [gameId, setGameId] = useState(81);

    useEffect(() => {
        const client = new Client({
            brokerURL: 'ws://localhost:8080/ws'
        });

        client.onConnect = (frame) => {
            setConnected(true);
            console.log('Connected: ' + frame);
            //subscribes and listens for responses to /topic/${gameId} if a response comes through
            //then anything inside the
            client.subscribe(`/topic/${gameId}`, (greeting) => {
                const parsedData = (JSON.parse(greeting.body).name)
                showGreeting(JSON.parse(greeting.body).name);
            });
        };

        client.onWebSocketError = (error) => {
            console.error('Error with websocket', error);
        };

        client.onStompError = (frame) => {
            console.error('Broker reported error: ' + frame.headers['message']);
            console.error('Additional details: ' + frame.body);
        };

        setStompClient(client); // Set the stompClient state

        return () => {
            client.deactivate();
            setConnected(false);
            console.log("Disconnected");
        }
    }, []);

    function connect() {
        if (stompClient) {
            stompClient.activate();
        }
    }

    function disconnect() {
        if (stompClient) {
            stompClient.deactivate();
            setConnected(false);
            console.log("Disconnected");
        }
    }

    function sendName() {
        if (stompClient) {
            stompClient.publish({
                destination: `/app/game/${gameId}`,
                body: JSON.stringify({'name': document.getElementById("name").value})
            });
        }
    }

    function showGreeting(message) {
        const greetingsTable = document.getElementById("greetings");
        const newRow = document.createElement("tr");
        const newCell = document.createElement("td");
        const newText = document.createTextNode(message);
        newCell.appendChild(newText);
        newRow.appendChild(newCell);
        greetingsTable.appendChild(newRow);
    }

    return (
        <div id="main-content" className="container">
            <div className="row">
                <div className="col-md-6">
                    <form className="form-inline" onSubmit={(e) => {e.preventDefault(); connect();}}>
                        <div className="form-group">
                            <label htmlFor="connect">WebSocket connection:</label>
                            <button id="connect" className="btn btn-default" type="submit" disabled={connected}>Connect</button>
                            <button id="disconnect" className="btn btn-default" type="button" disabled={!connected} onClick={disconnect}>Disconnect</button>
                        </div>
                    </form>
                </div>
                <div className="col-md-6">
                    <form className="form-inline" onSubmit={(e) => {e.preventDefault(); sendName();}}>
                        <div className="form-group">
                            <label htmlFor="name">What is your name?</label>
                            <input type="text" id="name" className="form-control" placeholder="Your name here..."/>
                        </div>
                        <button id="send" className="btn btn-default" type="submit">Send</button>
                    </form>
                </div>
            </div>
            <div className="row">
                <div className="col-md-12">
                    <table id="conversation" className="table table-striped">
                        <thead>
                            <tr>
                                <th>Greetings</th>
                            </tr>
                        </thead>
                        <tbody id="greetings">
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
}

export default Game;