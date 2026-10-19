import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { AuthService } from './auth.service';

export const authGuard: CanActivateFn = (route) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.estaLogueado()) {
    auth.logout(false);
    return router.createUrlTree(['/login']);
  }

  const roles = route.data['roles'] as string[] | undefined;
  if (roles && !auth.tieneRol(roles)) {
    return router.createUrlTree(['/no-autorizado']);
  }
  return true;
};