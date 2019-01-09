import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class RegistrationComponent implements OnInit {

  firstNameFormGroup: FormGroup;
  secondNameFormGroup: FormGroup;
  usernameFormGroup: FormGroup;
  passwordFormGroup: FormGroup;

  constructor(private _formBuilder: FormBuilder) {
  }

  ngOnInit() {
    this.firstNameFormGroup = this._formBuilder.group({
      firstNameCtrl: ['',
        Validators.compose([
          Validators.required,
          Validators.minLength(2),
          Validators.maxLength(40),
          Validators.pattern("[a-zA-Z]*")
        ])
      ]
    });
    this.secondNameFormGroup = this._formBuilder.group({
      secondNameCtrl: ['',
        Validators.compose([
          Validators.required,
          Validators.minLength(2),
          Validators.maxLength(40),
          Validators.pattern("[a-zA-Z]+")
        ])
      ]
    });
    this.usernameFormGroup = this._formBuilder.group({
      usernameCtrl: ['',
        Validators.compose([
          Validators.required,
          Validators.minLength(2),
          Validators.maxLength(40),
          Validators.pattern("[a-zA-Z0-9._]+")
        ])
      ]
    });
    this.passwordFormGroup = this._formBuilder.group({
      passwordCtrl: ['',
        Validators.compose([
          Validators.required,
          Validators.minLength(7),
          Validators.maxLength(255),
          Validators.pattern("[a-zA-Z0-9_$&#]+")
        ])
      ]
    })
  }

}
