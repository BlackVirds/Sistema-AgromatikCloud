export interface Huerta {
  nombre: string;
  ubicacionGeografica?: string;
  tamañoHectareas?: number;
  tipoSuelo?: "ARCILLOSO" | "ARENOSO" | "LIMOSO" | "FRANCO" | "OTROS";
  direccion?: string;
  municipio?: string;
  estado?: string;
  pais?: string;
  altitudMetros?: number;
  descripcion?: string;
}
