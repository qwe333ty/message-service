import {Component, OnInit} from '@angular/core';
import {FormControl, Validators} from "@angular/forms";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

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

  constructor() {
  }

  ngOnInit() {

  }

}
