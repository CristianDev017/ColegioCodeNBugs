import { Routes } from '@angular/router';

import { authGuard } from './core/auth.guard';

import { Login } from './pages/login/login';
import { Inicio } from './pages/inicio/inicio';
import { NoAutorizado } from './pages/no-autorizado/no-autorizado';

import { UsuariosAdmin } from './pages-superadmin/usuarios-admin/usuarios-admin';
import { Ciclos } from './pages-superadmin/ciclos/ciclos';
import { Carreras } from './pages-superadmin/carreras/carreras';
import { GradosDefecto } from './pages-superadmin/grados-defecto/grados-defecto';

import { Grados } from './pages-admin/grados/grados';
import { Cursos } from './pages-admin/cursos/cursos';
import { Secciones } from './pages-admin/secciones/secciones';
import { GestionCurriculo } from './pages-admin/curriculo/curriculo';
import { ListaTarifas } from './pages-admin/tarifas/lista-tarifas/lista-tarifas';
import { CrearTarifa } from './pages-admin/tarifas/crear-tarifa/crear-tarifa';
import { EditarTarifa } from './pages-admin/tarifas/editar-tarifa/editar-tarifa';
import { ListaActividades } from './pages-admin/actividades/lista-actividades/lista-actividades';
import { CrearActividad } from './pages-admin/actividades/crear-actividad/crear-actividad';
import { EditarActividad } from './pages-admin/actividades/editar-actividad/editar-actividad';
import { ListaEmpleados } from './pages-admin/empleados/lista-empleados/lista-empleados';
import { CrearEmpleado } from './pages-admin/empleados/crear-empleado/crear-empleado';
import { EditarEmpleado } from './pages-admin/empleados/editar-empleado/editar-empleado';
import { ListaAsignaciones } from './pages-admin/asignaciones/lista-asignaciones/lista-asignaciones';
import { CrearAsignacion } from './pages-admin/asignaciones/crear-asignacion/crear-asignacion';
import { EditarAsignacion } from './pages-admin/asignaciones/editar-asignacion/editar-asignacion';
import { ListaUsuariosOperativos } from './pages-admin/usuarios-operativos/lista-usuarios-operativos/lista-usuarios-operativos';
import { CrearUsuarioOperativo } from './pages-admin/usuarios-operativos/crear-usuario-operativo/crear-usuario-operativo';
import { EditarUsuarioOperativo } from './pages-admin/usuarios-operativos/editar-usuario-operativo/editar-usuario-operativo';


import { ListaEstudiantes } from './pages-secretaria/estudiantes/lista-estudiantes/lista-estudiantes';
import { CrearEstudiante } from './pages-secretaria/estudiantes/crear-estudiante/crear-estudiante';
import { EditarEstudiante } from './pages-secretaria/estudiantes/editar-estudiante/editar-estudiante';
import { ListaInscripciones } from './pages-secretaria/inscripciones/lista-inscripciones/lista-inscripciones';
import { CrearInscripcion } from './pages-secretaria/inscripciones/crear-inscripcion/crear-inscripcion';
import { ListaPagos } from './pages-secretaria/pagos/lista-pagos/lista-pagos';
import { CrearPago } from './pages-secretaria/pagos/crear-pago/crear-pago';
import { ComprobantePago } from './pages-secretaria/pagos/comprobante-pago/comprobante-pago';
import { ListaSolvencias } from './pages-secretaria/solvencias/lista-solvencias/lista-solvencias';
import { CrearSolvencia } from './pages-secretaria/solvencias/crear-solvencia/crear-solvencia';
import { ConstanciaSolvencia } from './pages-secretaria/solvencias/constancia-solvencia/constancia-solvencia';


import { MisCursos } from './pages-maestro/mis-cursos/mis-cursos';
import { CursoAsistencia } from './pages-maestro/curso-asistencia/curso-asistencia';
import { ListaPonderaciones } from './pages-maestro/ponderaciones/lista-ponderaciones/lista-ponderaciones';
import { CrearPonderacion } from './pages-maestro/ponderaciones/crear-ponderacion/crear-ponderacion';
import { EditarPonderacion } from './pages-maestro/ponderaciones/editar-ponderacion/editar-ponderacion';
import { CursoNotas } from './pages-maestro/curso-notas/curso-notas';

import { ListaLibros } from './pages-bibliotecario/libros/lista-libros/lista-libros';
import { CrearLibro } from './pages-bibliotecario/libros/crear-libro/crear-libro';
import { EditarLibro } from './pages-bibliotecario/libros/editar-libro/editar-libro';
import { ParametrosBiblioteca } from './pages-bibliotecario/parametros/parametros-biblioteca';
import { ListaPrestamos } from './pages-bibliotecario/prestamos/lista-prestamos/lista-prestamos';
import { CrearPrestamo } from './pages-bibliotecario/prestamos/crear-prestamo/crear-prestamo';
import { ListaDevoluciones } from './pages-bibliotecario/devoluciones/lista-devoluciones/lista-devoluciones';
import { ListaMultas } from './pages-bibliotecario/multas/lista-multas/lista-multas';


import { ListaPrestamosMora } from './pages-reportes/prestamos-mora/lista-prestamos-mora';
export const routes: Routes = [
  // publicas
  { path: 'login', component: Login },
  { path: 'no-autorizado', component: NoAutorizado },

  // cualquier usuario con sesion
  { path: 'inicio', component: Inicio, canActivate: [authGuard] },

  // super admin
  { path: 'usuarios-admin', component: UsuariosAdmin, canActivate: [authGuard], data: { roles: ['SUPER_ADMIN'] } },
  { path: 'ciclos', component: Ciclos, canActivate: [authGuard], data: { roles: ['SUPER_ADMIN'] } },
  { path: 'carreras', component: Carreras, canActivate: [authGuard], data: { roles: ['SUPER_ADMIN'] } },
  { path: 'grados-defecto', component: GradosDefecto, canActivate: [authGuard], data: { roles: ['SUPER_ADMIN'] } },

  // admin: catalogo
  { path: 'grados', component: Grados, canActivate: [authGuard], data: { roles: ['ADMIN'] } },
  { path: 'secciones', component: Secciones, canActivate: [authGuard], data: { roles: ['ADMIN'] } },
  { path: 'cursos', component: Cursos, canActivate: [authGuard], data: { roles: ['ADMIN'] } },
  { path: 'curriculo', component: GestionCurriculo, canActivate: [authGuard], data: { roles: ['ADMIN'] } },

  // admin: tarifas
  { path: 'tarifas', component: ListaTarifas, canActivate: [authGuard], data: { roles: ['ADMIN'] } },
  { path: 'tarifas/nueva', component: CrearTarifa, canActivate: [authGuard], data: { roles: ['ADMIN'] } },
  { path: 'tarifas/editar/:id', component: EditarTarifa, canActivate: [authGuard], data: { roles: ['ADMIN'] } },

  // admin: actividades
  { path: 'actividades', component: ListaActividades, canActivate: [authGuard], data: { roles: ['ADMIN'] } },
  { path: 'actividades/nueva', component: CrearActividad, canActivate: [authGuard], data: { roles: ['ADMIN'] } },
  { path: 'actividades/editar/:id', component: EditarActividad, canActivate: [authGuard], data: { roles: ['ADMIN'] } },


  { path: 'empleados', component: ListaEmpleados, canActivate: [authGuard], data: { roles: ['ADMIN'] } },
  { path: 'empleados/nuevo', component: CrearEmpleado, canActivate: [authGuard], data: { roles: ['ADMIN'] } },
  { path: 'empleados/editar/:id', component: EditarEmpleado, canActivate: [authGuard], data: { roles: ['ADMIN'] } },

  
  { path: 'asignaciones', component: ListaAsignaciones, canActivate: [authGuard], data: { roles: ['ADMIN'] } },
  { path: 'asignaciones/nueva', component: CrearAsignacion, canActivate: [authGuard], data: { roles: ['ADMIN'] } },
  { path: 'asignaciones/editar/:id', component: EditarAsignacion, canActivate: [authGuard], data: { roles: ['ADMIN'] } },
  
  { path: 'usuarios-operativos', component: ListaUsuariosOperativos, canActivate: [authGuard], data: { roles: ['ADMIN'] } },
  { path: 'usuarios-operativos/nuevo', component: CrearUsuarioOperativo, canActivate: [authGuard], data: { roles: ['ADMIN'] } },
  { path: 'usuarios-operativos/editar/:id', component: EditarUsuarioOperativo, canActivate: [authGuard], data: { roles: ['ADMIN'] } },
  
  

  
  
  // secretaria: estudiantes
  { path: 'estudiantes', component: ListaEstudiantes, canActivate: [authGuard], data: { roles: ['SECRETARIA', 'ADMIN'] } },
  { path: 'estudiantes/nuevo', component: CrearEstudiante, canActivate: [authGuard], data: { roles: ['SECRETARIA', 'ADMIN'] } },
  { path: 'estudiantes/editar/:id', component: EditarEstudiante, canActivate: [authGuard], data: { roles: ['SECRETARIA', 'ADMIN'] } },

  // secretaria: inscripciones
  { path: 'inscripciones', component: ListaInscripciones, canActivate: [authGuard], data: { roles: ['SECRETARIA', 'ADMIN'] } },
  { path: 'inscripciones/nueva', component: CrearInscripcion, canActivate: [authGuard], data: { roles: ['SECRETARIA'] } },


    // secretaria: pagos
  { path: 'pagos', component: ListaPagos, canActivate: [authGuard], data: { roles: ['SECRETARIA', 'ADMIN'] } },
  { path: 'pagos/nuevo', component: CrearPago, canActivate: [authGuard], data: { roles: ['SECRETARIA'] } },
  { path: 'pagos/comprobante/:id', component: ComprobantePago, canActivate: [authGuard], data: { roles: ['SECRETARIA', 'ADMIN'] } },
 
   // secretaria: solvencias
  { path: 'solvencias', component: ListaSolvencias, canActivate: [authGuard], data: { roles: ['SECRETARIA', 'ADMIN'] } },
  { path: 'solvencias/nueva', component: CrearSolvencia, canActivate: [authGuard], data: { roles: ['SECRETARIA'] } },
  { path: 'solvencias/constancia/:id', component: ConstanciaSolvencia, canActivate: [authGuard], data: { roles: ['SECRETARIA', 'ADMIN'] } },
  
  

  // maestro

  { path: 'mis-cursos', component: MisCursos, canActivate: [authGuard], data: { roles: ['MAESTRO'] } },
  { path: 'mis-cursos/:id/ponderaciones', component: ListaPonderaciones, canActivate: [authGuard], data: { roles: ['MAESTRO'] } },
  { path: 'mis-cursos/:id/ponderaciones/nueva', component: CrearPonderacion, canActivate: [authGuard], data: { roles: ['MAESTRO'] } },
  { path: 'mis-cursos/:id/ponderaciones/editar/:idPonderacion', component: EditarPonderacion, canActivate: [authGuard], data: { roles: ['MAESTRO'] } },
  { path: 'mis-cursos/:id/asistencia', component: CursoAsistencia, canActivate: [authGuard], data: { roles: ['MAESTRO'] } },
  { path: 'mis-cursos/:id/notas', component: CursoNotas, canActivate: [authGuard], data: { roles: ['MAESTRO'] } },


  // bibliotecario
  { path: 'libros', component: ListaLibros, canActivate: [authGuard], data: { roles: ['BIBLIOTECARIO'] } },
  { path: 'libros/nuevo', component: CrearLibro, canActivate: [authGuard], data: { roles: ['BIBLIOTECARIO'] } },
  { path: 'libros/editar/:id', component: EditarLibro, canActivate: [authGuard], data: { roles: ['BIBLIOTECARIO'] } },
  { path: 'biblioteca/parametros', component: ParametrosBiblioteca, canActivate: [authGuard], data: { roles: ['BIBLIOTECARIO'] } },
  { path: 'prestamos', component: ListaPrestamos, canActivate: [authGuard], data: { roles: ['BIBLIOTECARIO'] } },
  { path: 'prestamos/nuevo', component: CrearPrestamo, canActivate: [authGuard], data: { roles: ['BIBLIOTECARIO'] } },
  { path: 'devoluciones', component: ListaDevoluciones, canActivate: [authGuard], data: { roles: ['BIBLIOTECARIO'] } },
  { path: 'multas', component: ListaMultas, canActivate: [authGuard], data: { roles: ['BIBLIOTECARIO'] } },


  // reportes
{ path: 'reportes/prestamos-mora', component: ListaPrestamosMora, canActivate: [authGuard], data: { roles: ['BIBLIOTECARIO'] } },


  // redirecciones
  { path: '', pathMatch: 'full', redirectTo: 'inicio' },
  { path: '**', redirectTo: 'inicio' }
];
