import { Routes, Route } from "react-router-dom";
import { useState, useEffect } from "react";
import "./App.css";
import Game from "./components/Game";
import Home from "./components/Home";
import Login from "./components/Login";

function App() {
  const [csrfToken, setCsrfToken] = useState("");
  useEffect(() => {
    fetch("/api/csrf")
      .then((r) => r.json())
      .then((r) => {
        console.log(r);
        setCsrfToken(r.token);
      })
      .catch((error) => {
        console.error("Error fetching data:", error);
      });
  }, []);

  return (
    <div className="App">
      <Routes>
        <Route path="/game" element={<Game />} />
        <Route path="/login" element={<Login csrfToken={csrfToken} />} />
        <Route path="/" element={<Home csrfToken={csrfToken} />} />
      </Routes>
    </div>
  );
}

export default App;
