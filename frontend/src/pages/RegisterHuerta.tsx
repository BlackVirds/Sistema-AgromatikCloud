import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import type { Huerta } from "../types/Huerta";
import "../css/RegisterHuerta.css";

const RegisterHuerta = () => {
  const navigate = useNavigate();

  const [huerta, setHuerta] = useState<Huerta>({
    nombre: "",
    pais: "México",
  });

  // 🔔 MODAL GUARDAR
  const [showModal, setShowModal] = useState(false);
  const [modalMessage, setModalMessage] = useState("");
  const [isError, setIsError] = useState(false);

  // 🔴 MODAL CANCELAR
  const [showCancelModal, setShowCancelModal] = useState(false);

  // 🔁 CARGAR JSON TEMPORAL
  useEffect(() => {
    const temp = sessionStorage.getItem("registerHuertaTemp");
    if (temp) {
      setHuerta(JSON.parse(temp));
    }
  }, []);

  // ✏️ CAMBIOS EN INPUTS
  const handleChange = (
    e: React.ChangeEvent<
      HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement
    >
  ) => {
    const { name, value, type } = e.target;

    // 🚫 No permitir valores negativos
    if (
      type === "number" &&
      (name === "tamañoHectareas" || name === "altitudMetros") &&
      Number(value) < 0
    ) {
      return;
    }

    const updatedHuerta = {
      ...huerta,
      [name]: type === "number" ? Number(value) : value,
    };

    // 🔁 Si cambia el país, limpiar el estado
    if (name === "pais") {
      updatedHuerta.estado = "";
    }

    setHuerta(updatedHuerta);
    sessionStorage.setItem(
      "registerHuertaTemp",
      JSON.stringify(updatedHuerta)
    );
  };

  // 💾 GUARDAR HUERTA
  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    if (!huerta.nombre.trim()) {
      setModalMessage("El nombre de la huerta es obligatorio");
      setIsError(true);
      setShowModal(true);
      return;
    }

    const huertas: Huerta[] = JSON.parse(
      sessionStorage.getItem("huertas") || "[]"
    );

    huertas.push(huerta);

    sessionStorage.setItem("huertas", JSON.stringify(huertas));
    sessionStorage.setItem("huertaActiva", JSON.stringify(huerta));
    sessionStorage.removeItem("registerHuertaTemp");

    setModalMessage("Huerta registrada correctamente 🌱");
    setIsError(false);
    setShowModal(true);

    setTimeout(() => {
      navigate("/dashboard");
    }, 1000);
  };

  // ❌ ABRIR MODAL CANCELAR
  const handleCancel = () => {
    setShowCancelModal(true);
  };

  const estadosMexico = [
    "Aguascalientes",
    "Baja California",
    "Baja California Sur",
    "Campeche",
    "Chiapas",
    "Chihuahua",
    "Ciudad de México",
    "Coahuila",
    "Colima",
    "Durango",
    "Estado de México",
    "Guanajuato",
    "Guerrero",
    "Hidalgo",
    "Jalisco",
    "Michoacán",
    "Morelos",
    "Nayarit",
    "Nuevo León",
    "Oaxaca",
    "Puebla",
    "Querétaro",
    "Quintana Roo",
    "San Luis Potosí",
    "Sinaloa",
    "Sonora",
    "Tabasco",
    "Tamaulipas",
    "Tlaxcala",
    "Veracruz",
    "Yucatán",
    "Zacatecas",
  ];

  const estadosUSA = [
    "Alabama",
    "Alaska",
    "Arizona",
    "Arkansas",
    "California",
    "Colorado",
    "Connecticut",
    "Delaware",
    "Florida",
    "Georgia",
    "Hawaii",
    "Idaho",
    "Illinois",
    "Indiana",
    "Iowa",
    "Kansas",
    "Kentucky",
    "Louisiana",
    "Maine",
    "Maryland",
    "Massachusetts",
    "Michigan",
    "Minnesota",
    "Mississippi",
    "Missouri",
    "Montana",
    "Nebraska",
    "Nevada",
    "New Hampshire",
    "New Jersey",
    "New Mexico",
    "New York",
    "North Carolina",
    "North Dakota",
    "Ohio",
    "Oklahoma",
    "Oregon",
    "Pennsylvania",
    "Rhode Island",
    "South Carolina",
    "South Dakota",
    "Tennessee",
    "Texas",
    "Utah",
    "Vermont",
    "Virginia",
    "Washington",
    "West Virginia",
    "Wisconsin",
    "Wyoming",
  ];

  return (
    <div className="registerHuerta-container">
      <h2>Registrar Huerta</h2>

      <form className="registerHuerta-form" onSubmit={handleSubmit}>
        <input
          name="nombre"
          placeholder="Nombre de la huerta *"
          value={huerta.nombre}
          onChange={handleChange}
        />

        <input
          name="ubicacionGeografica"
          placeholder="Latitud,Longitud"
          value={huerta.ubicacionGeografica || ""}
          onChange={handleChange}
        />

        <input
          name="tamañoHectareas"
          type="number"
          step="0.01"
          placeholder="Tamaño (hectáreas)"
          value={huerta.tamañoHectareas ?? ""}
          onChange={handleChange}
        />

        <select
          name="tipoSuelo"
          value={huerta.tipoSuelo || ""}
          onChange={handleChange}
        >
          <option value="">Tipo de suelo</option>
          <option value="ARCILLOSO">ARCILLOSO</option>
          <option value="ARENOSO">ARENOSO</option>
          <option value="LIMOSO">LIMOSO</option>
          <option value="FRANCO">FRANCO</option>
          <option value="OTROS">OTROS</option>
        </select>

        <input
          name="direccion"
          placeholder="Dirección"
          value={huerta.direccion || ""}
          onChange={handleChange}
        />

        <input
          name="municipio"
          placeholder="Municipio"
          value={huerta.municipio || ""}
          onChange={handleChange}
        />

        <select
          name="estado"
          value={huerta.estado || ""}
          onChange={handleChange}
          disabled={!huerta.pais}
        >
          <option value="">Selecciona un estado</option>

          {huerta.pais === "México" &&
            estadosMexico.map((estado) => (
              <option key={estado} value={estado}>
                {estado}
              </option>
            ))}

          {huerta.pais === "Estados Unidos" &&
            estadosUSA.map((estado) => (
              <option key={estado} value={estado}>
                {estado}
              </option>
            ))}
        </select>


        <select
          name="pais"
          value={huerta.pais}
          onChange={handleChange}
        >
          <option value="">Selecciona un país</option>
          <option value="México">México</option>
          <option value="Estados Unidos">Estados Unidos</option>
        </select>


        <input
          name="altitudMetros"
          type="number"
          step="0.01"
          placeholder="Altitud (m)"
          value={huerta.altitudMetros ?? ""}
          onChange={handleChange}
        />

        <textarea
          name="descripcion"
          placeholder="Descripción"
          value={huerta.descripcion || ""}
          onChange={handleChange}
        />

        {/* BOTONES */}
        <div className="registerHuerta-buttons">
          <button type="submit" className="registerHuerta-saveBtn">
            Guardar Huerta
          </button>

          <button
            type="button"
            className="registerHuerta-cancelBtn"
            onClick={handleCancel}
          >
            Cancelar
          </button>
        </div>
      </form>

      {/* 🔔 MODAL GUARDAR */}
      {showModal && (
        <div className="registerHuerta-modalOverlay">
          <div className="registerHuerta-modal">
            <p
              className={
                isError
                  ? "registerHuerta-modalError"
                  : "registerHuerta-modalSuccess"
              }
            >
              {modalMessage}
            </p>

            <button
              className={
                isError
                  ? "registerHuerta-modalButtonError"
                  : "registerHuerta-modalButtonSuccess"
              }
              onClick={() => setShowModal(false)}
            >
              Aceptar
            </button>
          </div>
        </div>
      )}

      {/* 🔴 MODAL CANCELAR */}
      {showCancelModal && (
        <div className="registerHuerta-modalOverlay">
          <div className="registerHuerta-modal">
            <p className="registerHuerta-modalError">
              ¿Deseas cancelar el registro?
              <br />
              Se perderán los datos.
            </p>

            <div className="registerHuerta-buttons">
              <button
                className="registerHuerta-modalButtonError"
                onClick={() => {
                  sessionStorage.removeItem("registerHuertaTemp");
                  navigate("/dashboard");
                }}
              >
                Sí, cancelar
              </button>

              <button
                className="registerHuerta-modalButtonSuccess"
                onClick={() => setShowCancelModal(false)}
              >
                No, continuar
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default RegisterHuerta;
