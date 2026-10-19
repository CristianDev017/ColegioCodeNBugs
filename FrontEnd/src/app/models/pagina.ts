export class Pagina<T> {
  datos: T[] = [];
  total = 0;
  pagina = 1;
  tamano = 10;
}