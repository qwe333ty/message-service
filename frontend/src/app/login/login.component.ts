import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormControl, Validators} from "@angular/forms";
import {RequestService} from "../service/request/request.service";
import {TokenStorageService} from "../service/tokenStorage/token-storage.service";
import {Subscription} from "rxjs";
import {MatSnackBar} from "@angular/material";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {

  showProgressBar: boolean = false;
  wrongUserDetails: boolean = false;
  private subscriptions: Subscription[] = [];

  emailFormControl = new FormControl('', [
    Validators.required,
    Validators.pattern("[a-zA-Z0-9._%+-@]+"),
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
              public snackBar: MatSnackBar) {
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
            this.showProgressBar = false;
            this.openSnackBar('The username and password that you entered did not match records. Please double-check and try again.', "Close");
            return;
          }
          this.tokenStorageService.saveToken(token.tok);

          let jwtData = token.tok.split('.')[1];
          let decodedJwtJsonData = window.atob(jwtData);
          let decodedJwtData = JSON.parse(decodedJwtJsonData);

          console.log("Data from token", decodedJwtJsonData);

          this.showProgressBar = false;
        })
    );
  }

}
