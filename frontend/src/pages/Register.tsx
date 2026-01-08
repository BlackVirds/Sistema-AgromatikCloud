import { useState } from "react";
import "../css/Register.css";
import { useNavigate } from "react-router-dom";

const Register = () => {
  const navigate = useNavigate();

  const [nombre, setNombre] = useState("");
  const [apellido, setApellido] = useState("");
  const [email, setEmail] = useState("");
  const [telefono, setTelefono] = useState("");
  const [password, setPassword] = useState("");
  const [passwordRepeat, setPasswordRepeat] = useState("");
  const [tipo, setTipo] = useState("AGRICULTOR");
  const [subscriptionPlan] = useState("BASICO");

  const [showModal, setShowModal] = useState(false);
  const [modalMessage, setModalMessage] = useState("");
  const [isError, setIsError] = useState(false);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    if (
      !nombre ||
      !apellido ||
      !email ||
      !telefono ||
      !password ||
      !passwordRepeat
    ) {
      setModalMessage("Todos los campos son obligatorios");
      setIsError(true);
      setShowModal(true);
      return;
    }

    if (!/^\d{10}$/.test(telefono)) {
      setModalMessage("El teléfono debe tener exactamente 10 dígitos");
      setIsError(true);
      setShowModal(true);
      return;
    }

    if (password !== passwordRepeat) {
      setModalMessage("Las contraseñas no coinciden");
      setIsError(true);
      setShowModal(true);
      return;
    }

    const newUser = {
      nombre,
      apellido,
      email,
      telefono,
      password,
      tipo,
      subscriptionPlan,
    };

    localStorage.setItem("user", JSON.stringify(newUser));

    setModalMessage("Usuario creado correctamente 🎉");
    setIsError(false);
    setShowModal(true);
  };

  return (
    <div className="container-Register">
      <form className="form-Register" onSubmit={handleSubmit}>
        <h2>Crear Cuenta</h2>

        <input
          className="input-Register"
          placeholder="Nombre"
          value={nombre}
          onChange={(e) => setNombre(e.target.value)}
        />

        <input
          className="input-Register"
          placeholder="Apellido"
          value={apellido}
          onChange={(e) => setApellido(e.target.value)}
        />

        <input
          className="input-Register"
          type="email"
          placeholder="Correo"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />

        <input
          className="input-Register"
          placeholder="Teléfono (10 dígitos)"
          value={telefono}
          maxLength={10}
          onChange={(e) => setTelefono(e.target.value)}
        />

        <select
          className="input-Register"
          value={tipo}
          onChange={(e) => setTipo(e.target.value)}
        >
          <option value="AGRICULTOR">AGRICULTOR</option>
          <option value="EMPRESA">EMPRESA</option>
          <option value="COOPERATIVA">COOPERATIVA</option>
        </select>

        <select className="input-Register" value={subscriptionPlan} disabled>
          <option value="BASICO">BÁSICO</option>
        </select>

        <input
          className="input-Register"
          type="password"
          placeholder="Contraseña"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />

        <input
          className="input-Register"
          type="password"
          placeholder="Repetir contraseña"
          value={passwordRepeat}
          onChange={(e) => setPasswordRepeat(e.target.value)}
        />

        <button className="button-Register" type="submit">
          Crear cuenta
        </button>

        <div className="register-link">
          <span>¿Ya tienes cuenta?</span>
          <button
            type="button"
            className="register-button"
            onClick={() => navigate("/login")}
          >
            Inicia sesión
          </button>
        </div>
      </form>

      {showModal && (
        <div className="modal-overlay">
          <div className="modal">
            <p className={isError ? "modal-error" : "modal-success"}>
              {modalMessage}
            </p>
            <button
              className="modal-button"
              onClick={() => {
                setShowModal(false);
                if (!isError) navigate("/login");
              }}
            >
              Aceptar
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default Register;
