import { Routes, Route } from "react-router-dom";
import { useState, useEffect } from "react";
import "./App.css";
import Game from "./components/game_logic/Game";
import Home from "./components/Home";
import Login from "./components/Login";
import NavBar from "./components/NavBar";
import { UserProvider } from "./context/User"

function App() {
  const [csrfToken, setCsrfToken] = useState("");
  useEffect(() => {
    fetch("/api/csrf")
      .then((r) => r.json())
      .then((r) => {
        setCsrfToken(r.token);
      })
      .catch((error) => {
        console.error("Error fetching data:", error);
      });
  }, []);

  return (
    <div className="App">
        <UserProvider>
      <NavBar/>
      <Routes>
        <Route path="/game" element={<Game />} />
        <Route path="/login" element={<Login csrfToken={csrfToken} />} />
        <Route path="/" element={<Home csrfToken={csrfToken} />} />
      </Routes>
      </UserProvider>
    </div>
  );
}

export default App;
