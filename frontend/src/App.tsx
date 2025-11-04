import { useState } from "react";

import AlertsDashboard from "./pages/alertsDashboard";
import "./App.css";

function App() {
  const [count, setCount] = useState(0);

  return (
    <div style={{ fontFamily: "Arial, sans-serif" }}>
      <AlertsDashboard /> {/* 👈 Renderizar el Dashboard */}
    </div>
  );
}

export default App;
