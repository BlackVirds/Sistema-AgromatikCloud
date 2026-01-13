import { useState } from "react";
import { useNavigate } from "react-router-dom";
import type { Huerta } from "../types/Huerta";
import "../css/Dashboard.css";

/* ===== TIPOS ===== */
type SearchField =
  | "nombre"
  | "ubicacionGeografica"
  | "tamañoHectareas"
  | "tipoSuelo"
  | "direccion"
  | "municipio"
  | "estado"
  | "pais"
  | "altitudMetros"
  | "descripcion";

/* ===== CONSTANTES ===== */
const gridOptions: Array<3 | 5 | 7> = [3, 5, 7];

const estadosMexico = [
  "Aguascalientes", "Baja California", "Baja California Sur", "Campeche",
  "Chiapas", "Chihuahua", "Ciudad de México", "Coahuila", "Colima",
  "Durango", "Estado de México", "Guanajuato", "Guerrero", "Hidalgo",
  "Jalisco", "Michoacán", "Morelos", "Nayarit", "Nuevo León", "Oaxaca",
  "Puebla", "Querétaro", "Quintana Roo", "San Luis Potosí", "Sinaloa",
  "Sonora", "Tabasco", "Tamaulipas", "Tlaxcala", "Veracruz",
  "Yucatán", "Zacatecas",
];

const estadosUSA = [
  "Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado",
  "Connecticut", "Delaware", "Florida", "Georgia", "Hawaii", "Idaho",
  "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky", "Louisiana",
  "Maine", "Maryland", "Massachusetts", "Michigan", "Minnesota",
  "Mississippi", "Missouri", "Montana", "Nebraska", "Nevada",
  "New Hampshire", "New Jersey", "New Mexico", "New York",
  "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon",
  "Pennsylvania", "Rhode Island", "South Carolina", "South Dakota",
  "Tennessee", "Texas", "Utah", "Vermont", "Virginia", "Washington",
  "West Virginia", "Wisconsin", "Wyoming",
];

const tiposSuelo = [
  "ARCILLOSO",
  "ARENOSO",
  "LIMOSO",
  "FRANCO",
  "OTROS",
];

const Dashboard = () => {
  const navigate = useNavigate();

  const [cols, setCols] = useState<3 | 5 | 7>(3);
  const [searchField, setSearchField] = useState<SearchField>("nombre");
  const [searchTerm, setSearchTerm] = useState("");
  const [filterPais, setFilterPais] =
    useState<"" | "México" | "Estados Unidos">("");

  const huertas: Huerta[] = JSON.parse(
    sessionStorage.getItem("huertas") || "[]"
  );

  const huertaTemp: Huerta | null = JSON.parse(
    sessionStorage.getItem("registerHuertaTemp") || "null"
  );

  /* ===== FILTRO ===== */
  const huertasFiltradas = huertas.filter((h) => {
    if (!searchTerm) return true;

    if (searchField === "estado") {
      return (
        h.estado === searchTerm &&
        (!filterPais || h.pais === filterPais)
      );
    }

    const value = h[searchField];
    if (!value) return false;

    return String(value)
      .toLowerCase()
      .includes(searchTerm.toLowerCase());
  });

  const handleLogout = () => {
    sessionStorage.clear();
    navigate("/login");
  };

  const estados =
    filterPais === "México"
      ? estadosMexico
      : filterPais === "Estados Unidos"
      ? estadosUSA
      : [];

  return (
    <div className="dashboard">
      {/* ===== HEADER ===== */}
      <div className="dashboard-header">
        <h1>Huertas</h1>

        <div className="dashboard-actions">
          <button
            className="add-huerta-btn"
            onClick={() => navigate("/register-huerta")}
          >
            + Nueva Huerta
          </button>

          <div className="grid-controls">
            {gridOptions.map((n) => (
              <button
                key={n}
                className={cols === n ? "active" : ""}
                onClick={() => setCols(n)}
              >
                {n}
              </button>
            ))}
          </div>

          <button className="logout-btn" onClick={handleLogout}>
            Salir
          </button>
        </div>
      </div>

      {/* ===== BUSCADOR ===== */}
      <div className="dashboard-search-controls">
        <select
          value={searchField}
          onChange={(e) => {
            setSearchField(e.target.value as SearchField);
            setSearchTerm("");
            setFilterPais("");
          }}
        >
          <option value="nombre">Nombre</option>
          <option value="municipio">Municipio</option>
          <option value="estado">Estado</option>
          <option value="pais">País</option>
          <option value="tipoSuelo">Tipo de suelo</option>
          <option value="direccion">Dirección</option>
          <option value="ubicacionGeografica">Ubicación</option>
          <option value="tamañoHectareas">Tamaño (ha)</option>
          <option value="altitudMetros">Altitud</option>
          <option value="descripcion">Descripción</option>
        </select>

        {/* PAÍS */}
        {searchField === "pais" && (
          <select
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          >
            <option value="">Selecciona país</option>
            <option value="México">México</option>
            <option value="Estados Unidos">Estados Unidos</option>
          </select>
        )}

        {/* ESTADO */}
        {searchField === "estado" && (
          <>
            <select
              value={filterPais}
              onChange={(e) => {
                setFilterPais(e.target.value as any);
                setSearchTerm("");
              }}
            >
              <option value="">Selecciona país</option>
              <option value="México">México</option>
              <option value="Estados Unidos">Estados Unidos</option>
            </select>

            <select
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              disabled={!filterPais}
            >
              <option value="">Selecciona estado</option>
              {estados.map((estado) => (
                <option key={estado} value={estado}>
                  {estado}
                </option>
              ))}
            </select>
          </>
        )}

        {/* TIPO DE SUELO */}
        {searchField === "tipoSuelo" && (
          <select
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          >
            <option value="">Tipo de suelo</option>
            {tiposSuelo.map((t) => (
              <option key={t} value={t}>
                {t}
              </option>
            ))}
          </select>
        )}

        {/* INPUT NORMAL */}
        {searchField !== "pais" &&
          searchField !== "estado" &&
          searchField !== "tipoSuelo" && (
            <input
              type="text"
              placeholder={`Buscar por ${searchField}`}
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          )}
      </div>

      {/* ===== HUERTA EN PROCESO ===== */}
      {huertaTemp && (
        <div className="huerta-temp">
          <h3>Huerta en registro ⏳</h3>
          <p><strong>Nombre:</strong> {huertaTemp.nombre || "Sin nombre"}</p>
          <p><strong>País:</strong> {huertaTemp.pais}</p>

          <button onClick={() => navigate("/register-huerta")}>
            Continuar registro
          </button>
        </div>
      )}

      {/* ===== RESULTADOS ===== */}
      {huertasFiltradas.length === 0 && (
        <p className="no-huertas">No se encontraron huertas</p>
      )}

      <div className={`huertas-grid cols-${cols}`}>
        {huertasFiltradas.map((h, index) => (
          <div key={index} className="huerta-card">
            <h3>{h.nombre}</h3>
            {h.municipio && <p>Municipio: {h.municipio}</p>}
            {h.estado && <p>Estado: {h.estado}</p>}
            {h.pais && <p>País: {h.pais}</p>}

            <button
              onClick={() => {
                sessionStorage.setItem(
                  "huertaActiva",
                  JSON.stringify(h)
                );
                navigate("/huerta-dashboard");
              }}
            >
              Ver huerta
            </button>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Dashboard;
