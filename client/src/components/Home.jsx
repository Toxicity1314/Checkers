import React, { useState, useEffect, useMemo, useContext } from "react";
import { NavLink } from "react-router-dom";
import { useNavigate } from "react-router-dom";
import { UserContext } from "../context/User";



function Home({ csrfToken }) {
  const [buttonPressed, setButtonPressed] = useState(true);
  const navigate = useNavigate();
  const { user, setUser } = useContext(UserContext);


  const startNewGame = () =>{
      fetch(`/api/game`,{
          method: "post",
          headers: {
              "Content-Type": "application/json",
              "X-CSRF-TOKEN": csrfToken,
          },
          body: JSON.stringify({player1_id: 1, player2_id: 3})
      }).then(r =>{
          if(r.ok){
              r.json()
              .then( r =>{
                  console.log(r);
                  navigate('/game', { state: {game: r}})
              })
          }
         })
  }

  const resumeGame = () =>{
      fetch(`/api/game/21`,{
          method: "get",
          headers: {
              "Content-Type": "application/json",
              "X-CSRF-TOKEN": csrfToken,
          },
      }).then(r =>{
          if(r.ok){
              r.json()
              .then( r =>{
                  console.log(r);
                  navigate(`/game`, {state: {game: r}})
              })

          }
      })

      }

  const handleClick = () => {
//     fetch(`/api/logout`, {
//       method: "POST",
//       headers: {
//         "Content-Type": "application/json",
//         "X-CSRF-TOKEN": csrfToken,
//       },
//     }).then((r) => {
//             if (r.ok){
//               setUser({ username: false, authorities: false })
//             }else{
//               console.error("Error fetching data:", r);
//             }
//             })
    fetch(`/api/checkSession`, {
      headers: {
        "X-CSRF-TOKEN": csrfToken,
      },
    }).then(response => {
          if (!response.ok) {
              throw new Error('Network response was not ok');
          }
          return response.text(); // or response.json() if you're returning JSON
      })
      .then(data => {
          console.log(data); // This will log the "Authenticated user: ..." message
      })
      .catch(error => {
          console.error('There was a problem with the fetch operation:', error);
      });
  };

  return (
    <div>
      <button onClick={() => handleClick()}>Logout</button>
      <button onClick={() => startNewGame()}> start game </button>
      <button onClick={() => resumeGame()}> resume game </button>
    </div>
  );
}
export default Home;
