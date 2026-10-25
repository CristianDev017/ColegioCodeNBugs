import { Curso } from './curso';

export interface Curriculo {
  id: number;
  idCiclo: number;
  idGrado: number;
  cursos: Curso[];
}