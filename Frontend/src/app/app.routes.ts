
import { Routes } from '@angular/router';

import { Login } from './pages/login/login';
import { Dashboard } from './pages-superadmin/dashboard/dashboard';
import { CarrerasPagina } from './pages-superadmin/carreras/carreras';
import { UsuariosPagina } from './pages-superadmin/usuarios/usuarios';
import { GradosPagina } from './pages-superadmin/grados/grados';
import { CiclosPagina } from './pages-superadmin/ciclos/ciclos';

import { DashboardAdmin } from './pages-admin/dashboard/dashboard';
import { CursosPagina } from './pages-admin/cursos/cursos';
import { SeccionesPagina } from './pages-admin/secciones/secciones';

import { authGuard } from './guards/auth-guard';

export const routes: Routes = [
  {
    path: 'login',
    component: Login
  },
  {
    path: 'superadmin',
    component: Dashboard,
    canActivate: [authGuard],
    data: { roles: ['SUPER_ADMIN'] }
  },
  {
    path: 'superadmin/carreras',
    component: CarrerasPagina,
    canActivate: [authGuard],
    data: { roles: ['SUPER_ADMIN'] }
  },
  {
    path: 'superadmin/usuarios',
    component: UsuariosPagina,
    canActivate: [authGuard],
    data: { roles: ['SUPER_ADMIN'] }
  },
  {
    path: 'superadmin/grados',
    component: GradosPagina,
    canActivate: [authGuard],
    data: { roles: ['SUPER_ADMIN'] }
  },
  {
    path: 'superadmin/ciclos',
    component: CiclosPagina,
    canActivate: [authGuard],
    data: { roles: ['SUPER_ADMIN'] }
  },
  {
    path: 'admin',
    component: DashboardAdmin,
    canActivate: [authGuard],
    data: { roles: ['ADMIN', 'SUPER_ADMIN'] }
  },
  {
    path: 'admin/cursos',
    component: CursosPagina,
    canActivate: [authGuard],
    data: { roles: ['ADMIN', 'SUPER_ADMIN'] }
  },
  {
    path: 'admin/secciones',
    component: SeccionesPagina,
    canActivate: [authGuard],
    data: { roles: ['ADMIN', 'SUPER_ADMIN'] }
  },
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  }
];
