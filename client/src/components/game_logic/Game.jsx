import React, { useState, useEffect } from "react";
import { Client } from "@stomp/stompjs";
import GameGrid from "./GameGrid";
import { useLocation } from "react-router-dom";
import PlayerUI from "./PlayerUI";
function Game({ csrfToken }) {
  const [connected, setConnected] = useState(false);
  const [stompClient, setStompClient] = useState(null);
  const location = useLocation();
  const { game } = location.state;
  const [gameState, setGame] = useState(game);
  const gameId = game.id;

  useEffect(() => {
    const client = new Client({
      brokerURL: "/ws/ws", //url is this way to utilize vite proxy in order to send the web socket from the same origin
      // as the rest of the site. This will allow for the JSESSIONID to be attached with
      //websocket requests so that websocket requests can utilize it for authorization
      //                 connectHeaders:{
      //                     'X-CSRF-TOKEN': csrfToken},    do I need this for production?
      onConnect: (frame) => {
        setConnected(true);
        console.log("Connected: " + frame);
        client.subscribe(`/topic/${gameId}`, (message) => {
          const parsedData = JSON.parse(message.body);
          setGame(parsedData);
          console.log("parsedData");
          console.log(parsedData);
        });
        client.subscribe("/user/queue/errors", (message) => {
          alert(message.body);
        });
      },
      onWebSocketError: (error) => {
        console.error("Error with websocket", error);
      },
      onStompError: (frame) => {
        console.error("Broker reported error: " + frame.headers["message"]);
        console.error("Additional details: " + frame.body);
      },
    });

    client.activate(); // Activate the client
    setStompClient(client);

    // Cleanup function to deactivate the client when the component unmounts
    return () => {
      client.deactivate();
      setConnected(false);
      console.log("Disconnected");
    };
  }, [gameId]);

  return (
    <div id="gamePage">
      <PlayerUI
        gamePlayers={gameState.players}
        playerTurn={gameState.player_turn}
      />

      <div className="boardContainer">
        <GameGrid game={gameState} stompClient={stompClient} />
      </div>
    </div>
  );
}
export default Game;
