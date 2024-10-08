import { useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import { UserContext } from "../context/User";
import Login from "./Login";
function NavBar({ csrfToken }) {
  const [buttonPressed, setButtonPressed] = useState(true);
  const [isLogin, setIsLogin] = useState(false);
  const navigate = useNavigate();
  const { user, setUser } = useContext(UserContext);

  const startNewGame = () => {
    fetch(`/api/game`, {
      method: "post",
      headers: {
        "Content-Type": "application/json",
        "X-CSRF-TOKEN": csrfToken,
      },
      body: JSON.stringify({ player1_id: 1, player2_id: 3 }),
    }).then((r) => {
      if (r.ok) {
        r.json().then((r) => {
          console.log(r);
          navigate("/game", { state: { game: r } });
        });
      }
    });
  };

  const resumeGame = () => {
    fetch(`/api/game/21`, {
      method: "get",
      headers: {
        "Content-Type": "application/json",
        "X-CSRF-TOKEN": csrfToken,
      },
    }).then((r) => {
      if (r.ok) {
        r.json().then((r) => {
          console.log(r);
          navigate(`/game`, { state: { game: r } });
        });
      }
    });
  };

  const handleLogout = () => {
    fetch(`/api/logout`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "X-CSRF-TOKEN": csrfToken,
      },
    }).then((r) => {
      if (r.ok) {
        setUser({ username: false, authorities: false });
        navigate("/");
      } else {
        console.error("Error fetching data:", r);
      }
    });
  };

  const handleLogin = () => {
    setIsLogin(true);
  };
  return (
    <header>
      <h1> Checkers </h1>
      {isLogin && <Login csrfToken={csrfToken} setIsLogin={setIsLogin} />}
      {!user.username && (
        <div className="navButtons">
          <button onClick={() => handleLogin()}> Login </button>
          <button onClick={() => handleLogin()}> Signup </button>
        </div>
      )}
      {user.username && (
        <div className="navButtons">
          <button onClick={() => startNewGame()}> start game </button>
          <button onClick={() => resumeGame()}> resume game </button>
          <button onClick={() => navigate(`/`)}> home </button>
          <button onClick={() => handleLogout()}>Logout</button>
        </div>
      )}
    </header>
  );
}
export default NavBar;
