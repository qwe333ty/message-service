import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormControl, Validators} from "@angular/forms";
import {RequestService} from "../service/request/request.service";
import {TokenStorageService} from "../service/tokenStorage/token-storage.service";
import {Subscription} from "rxjs";
import {MatSnackBar} from "@angular/material";
import {ShareResourceService} from "../service/share/share-resource.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {

  showProgressBar: boolean = false;
  wrongUserDetails: boolean = false;
  authenticated: boolean = false;
  private subscriptions: Subscription[] = [];

  emailFormControl = new FormControl('', [
    Validators.required,
    Validators.pattern("[a-zA-Z0-9._%+-@]+"),
    Validators.minLength(2),
    Validators.maxLength(40),
  ]);

  passwordFormControl = new FormControl('', [
    Validators.required,
    Validators.minLength(7),
    Validators.maxLength(255),
    Validators.pattern("[a-zA-Z0-9_$&#]+")
  ]);

  constructor(private requestService: RequestService,
              private tokenStorageService: TokenStorageService,
              private snackBar: MatSnackBar,
              private resourceService: ShareResourceService) {
  }

  ngOnInit() {

  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  openSnackBar(message: string, action: string) {
    this.snackBar.open(message, action, {
      duration: 3500,
    });
  }

  login() {
    this.showProgressBar = true;

    this.subscriptions.push(
      this.requestService
        .getToken(this.emailFormControl.value, this.passwordFormControl.value)
        .subscribe(token => {
          if (!token || !token.tok || token.tok === '') {
            this.wrongUserDetails = true;
            this.authenticated = false;
            this.showProgressBar = false;
            this.openSnackBar('The username and password that you entered did not match records. Please double-check and try again.', "Close");
            return;
          }

          this.tokenStorageService.saveToken(token.tok);
          this.wrongUserDetails = false;

          this.authenticated = true;
          this.resourceService.nextAuthorizedObs(this.authenticated);

          this.showProgressBar = false;
        })
    );
  }

}
