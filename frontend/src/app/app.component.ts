import {Component, OnInit} from '@angular/core';
import {FormControl} from '@angular/forms';
import {Observable} from 'rxjs';
import {map, startWith} from 'rxjs/operators';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  authenticated: boolean = false;

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

  messageTypes: any[] = [
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

  ngOnInit() {
    this.filteredOptions = this.myControl.valueChanges.pipe(
      startWith(''),
      map(value => this._filter(value))
    );
  }

  private _filter(value: string): string[] {
    const filterValue = value.toLowerCase();

    return this.options.filter(option => option.toLowerCase().indexOf(filterValue) === 0);
  }
}
