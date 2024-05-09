import React, { useState, useEffect } from "react";

// create the context
const UserContext = React.createContext();

// create a provider component
function UserProvider({ children }) {
  const [user, setUser] = useState({ username: false, authorities: false });
  console.log(user)
  useEffect(() => {
    fetch("/api/auth").then((res) => {
      if (res.ok) {
        res.json().then((user) => setUser(user));
      }
    });
  }, []);
  // the value prop of the provider will be our context data
  // this value will be available to child components of this provider
  return (
    <UserContext.Provider value={{ user, setUser }}>
      {children}
    </UserContext.Provider>
  );
}

export { UserContext, UserProvider };
