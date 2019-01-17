import {Injectable} from '@angular/core';
import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
  HttpResponse
} from "@angular/common/http";
import {Observable} from "rxjs";
import {Router} from "@angular/router";
import {TokenStorageService} from "../tokenStorage/token-storage.service";
import {tap} from "rxjs/operators";
import {MatSnackBar} from "@angular/material";

@Injectable({
  providedIn: 'root'
})
export class InterceptorService implements HttpInterceptor {

  constructor(private tokenStorage: TokenStorageService,
              private router: Router,
              private snackBar: MatSnackBar) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    let authReq = req;
    if (this.tokenStorage.getToken() != null) {
      authReq = req.clone({
        headers:
          req.headers.set('Authorization', 'Bearer ' + this.tokenStorage.getToken())
      });
    }
    return next.handle(authReq).pipe(tap((event: HttpEvent<any>) => {
        if (event instanceof HttpResponse) {
          console.log(event);
        }
      }, (err: any) => {
        if (err instanceof HttpErrorResponse) {
          if (err.status === 401) {
            this.router.navigate(['login']);
            this.snackBar.open('Unauthorized access', "Close", {
              duration: 3500,
            });
          }
        }
      }
    ));
  }
}
