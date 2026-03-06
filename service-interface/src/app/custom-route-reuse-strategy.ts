import {ActivatedRouteSnapshot, DetachedRouteHandle, RouteReuseStrategy} from '@angular/router';

export class CustomRouteReuseStrategy implements RouteReuseStrategy {

  private cache = new Map<string, DetachedRouteHandle>();

  /** Salvăm componenta când navigăm DEPARTE de ea? */
  shouldDetach(route: ActivatedRouteSnapshot): boolean {
    return route.data['reuse'] === true;
  }

  /** Spring stochează componenta detașată */
  store(route: ActivatedRouteSnapshot, handle: DetachedRouteHandle | null): void {
    const key = route.routeConfig?.path ?? '';
    if (handle) {
      this.cache.set(key, handle);
    }
  }

  /** Refolosim o componentă din cache când navigăm SPRE ea? */
  shouldAttach(route: ActivatedRouteSnapshot): boolean {
    const key = route.routeConfig?.path ?? '';
    return this.cache.has(key);
  }

  /** Returnăm componenta din cache */
  retrieve(route: ActivatedRouteSnapshot): DetachedRouteHandle | null {
    const key = route.routeConfig?.path ?? '';
    return this.cache.get(key) ?? null;
  }

  /** Pe același route (ex: form/:id → alt id) refolosim aceeași instanță */
  shouldReuseRoute(future: ActivatedRouteSnapshot, curr: ActivatedRouteSnapshot): boolean {
    return future.routeConfig === curr.routeConfig;
  }
}
