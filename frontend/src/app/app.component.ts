import {Component, OnInit} from '@angular/core';
import {FormControl} from '@angular/forms';
import {Observable, of} from 'rxjs';
import {concatMap, map, startWith} from 'rxjs/operators';
import {Event, NavigationCancel, NavigationEnd, NavigationError, NavigationStart, Router} from "@angular/router";
import {ShareResourceService} from "./service/share/share-resource.service";
import {TokenStorageService} from "./service/tokenStorage/token-storage.service";
import {RequestService} from "./service/request/request.service";
import {User} from "./entity/User";
import {MatSnackBar} from "@angular/material";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  loading: boolean = false;
  authenticated: boolean = false;

  userDetails: User = new User();

  unauthenticatedMessageTypes: any[] = [
    {
      name: 'Log In',
      symbol: 'done',
      routerLink: 'login'
    },
    {
      name: 'Sign Up',
      symbol: 'done_all',
      routerLink: 'registration'
    }
  ];

  authenticatedMessageTypes: any[] = [
    {
      name: 'Inbox',
      symbol: 'inbox',
      routerLink: 'inbox'
    },
    {
      name: 'Starred',
      symbol: 'star_rate',
      routerLink: '.'
    },
    {
      name: 'Snoozed',
      symbol: 'snooze',
      routerLink: '.'
    },
    {
      name: 'Important',
      symbol: 'label_important',
      routerLink: '.'
    },
    {
      name: 'Sent',
      symbol: 'send',
      routerLink: '.'
    }
  ];

  myControl = new FormControl();
  options: string[] = ['One', 'Two', 'Three'];
  filteredOptions: Observable<string[]>;

  constructor(private router: Router,
              private snackBar: MatSnackBar,
              private resourceService: ShareResourceService,
              private tokenStorageService: TokenStorageService,
              private requestService: RequestService) {
    this.router.events.subscribe((event: Event) => {
      switch (true) {
        case event instanceof NavigationStart: {
          setTimeout(() => this.loading = true);
          break;
        }

        case event instanceof NavigationEnd:
        case event instanceof NavigationCancel:
        case event instanceof NavigationError: {
          setTimeout(() => this.loading = false);
          break;
        }
        default: {
          break;
        }
      }
    });
  }

  ngOnInit() {
    this.filteredOptions = this.myControl.valueChanges.pipe(
      startWith(''),
      map(value => this._filter(value))
    );
    this.resourceService.authorized$.pipe(
      concatMap(value => {
        this.authenticated = value;

        if (!value) {
          return of(null);
        }
        let token = this.tokenStorageService.getToken();
        let jwtData = token.split('.')[1];
        let decodedJwtJsonData = window.atob(jwtData);
        let decodedJwtData = JSON.parse(decodedJwtJsonData);

        return this.requestService.findUserByUsername(decodedJwtData.sub);
      })
    ).subscribe(value => {
      if (value && this.requestService.checkTokenInStorage()) {
        this.userDetails = value;
        this.requestService.userId = value.id;
        this.resourceService.nextUserId(value.id);
        this.router.navigate(['inbox']);
      }
    });
  }

  private _filter(value: string): string[] {
    const filterValue = value.toLowerCase();
    return this.options.filter(option => option.toLowerCase().indexOf(filterValue) === 0);
  }

  logout() {
    this.authenticated = false;
    this.tokenStorageService.signOut();
  }
}
