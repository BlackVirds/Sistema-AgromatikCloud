import { useState } from "react";
import { useNavigate } from "react-router-dom";
import type { Huerta } from "../types/Huerta";

const RegisterHuerta = () => {
  const navigate = useNavigate();

  const [huerta, setHuerta] = useState<Huerta>({
    nombre: "",
    pais: "México",
  });

  const handleChange = (
    e: React.ChangeEvent<
      HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement
    >
  ) => {
    const { name, value, type } = e.target;

    setHuerta((prev) => ({
      ...prev,
      [name]: type === "number" ? Number(value) : value,
    }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    if (!huerta.nombre.trim()) {
      alert("El nombre de la huerta es obligatorio");
      return;
    }

    // ✅ OBTENER HUERTAS EXISTENTES
    const huertas: Huerta[] = JSON.parse(
      sessionStorage.getItem("huertas") || "[]"
    );

    // ✅ GUARDAR NUEVA HUERTA
    huertas.push(huerta);
    sessionStorage.setItem("huertas", JSON.stringify(huertas));

    // ✅ MARCAR COMO ACTIVA
    sessionStorage.setItem("huertaActiva", JSON.stringify(huerta));

    // ✅ IR AL DASHBOARD
    navigate("/dashboard");
  };

  return (
    <div className="container">
      <h2>Registrar Huerta</h2>

      <form onSubmit={handleSubmit}>
        <input
          name="nombre"
          placeholder="Nombre de la huerta *"
          value={huerta.nombre}
          onChange={handleChange}
        />

        <input
          name="ubicacionGeografica"
          placeholder="Latitud,Longitud"
          onChange={handleChange}
        />

        <input
          name="tamañoHectareas"
          type="number"
          step="0.01"
          placeholder="Tamaño (hectáreas)"
          onChange={handleChange}
        />

        <select name="tipoSuelo" onChange={handleChange}>
          <option value="">Tipo de suelo</option>
          <option value="ARCILLOSO">ARCILLOSO</option>
          <option value="ARENOSO">ARENOSO</option>
          <option value="LIMOSO">LIMOSO</option>
          <option value="FRANCO">FRANCO</option>
          <option value="OTROS">OTROS</option>
        </select>

        <input name="direccion" placeholder="Dirección" onChange={handleChange} />
        <input name="municipio" placeholder="Municipio" onChange={handleChange} />
        <input name="estado" placeholder="Estado" onChange={handleChange} />

        <input
          name="pais"
          placeholder="País"
          value={huerta.pais}
          onChange={handleChange}
        />

        <input
          name="altitudMetros"
          type="number"
          step="0.01"
          placeholder="Altitud (m)"
          onChange={handleChange}
        />

        <textarea
          name="descripcion"
          placeholder="Descripción"
          onChange={handleChange}
        />

        <button type="submit">Guardar Huerta</button>
      </form>
    </div>
  );
};

export default RegisterHuerta;
