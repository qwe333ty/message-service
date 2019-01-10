import {Component, OnInit} from '@angular/core';
import {FormControl} from '@angular/forms';
import {Observable} from 'rxjs';
import {map, mergeMap, startWith} from 'rxjs/operators';
import {Event, NavigationCancel, NavigationEnd, NavigationError, NavigationStart, Router} from "@angular/router";
import {ShareResourceService} from "./service/share/share-resource.service";
import {TokenStorageService} from "./service/tokenStorage/token-storage.service";
import {RequestService} from "./service/request/request.service";
import {User} from "./entity/User";

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
      routerLink: '.'
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
              private resourceService: ShareResourceService,
              private tokenStorageService: TokenStorageService,
              private requestService: RequestService) {
    this.router.events.subscribe((event: Event) => {
      switch (true) {
        case event instanceof NavigationStart: {
          this.loading = true;
          break;
        }

        case event instanceof NavigationEnd:
        case event instanceof NavigationCancel:
        case event instanceof NavigationError: {
          this.loading = false;
          break;
        }
        default: {
          break;
        }
      }
    });
  }

  ngOnInit() {
    this.userDetails.username = 'qwe';
    this.filteredOptions = this.myControl.valueChanges.pipe(
      startWith(''),
      map(value => this._filter(value))
    );
    this.resourceService.authorized$.pipe(
      mergeMap(value => {
        this.authenticated = true;

        let jwtData = this.tokenStorageService.getToken().split('.')[1];
        let decodedJwtJsonData = window.atob(jwtData);
        let decodedJwtData = JSON.parse(decodedJwtJsonData);

        return this.requestService.findUserByUsername(decodedJwtData.sub);
      })
    ).subscribe(value => {
      this.userDetails = value;
    });
  }

  private _filter(value: string): string[] {
    const filterValue = value.toLowerCase();
    return this.options.filter(option => option.toLowerCase().indexOf(filterValue) === 0);
  }
}
