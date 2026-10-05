import {
  Component
} from '@angular/core';

import {
  CommonModule
} from '@angular/common';

import {
  Router,
  RouterLink
} from '@angular/router';

import {
  AuthService
} from '../../core/services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink
  ],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class Navbar {

  isAdmin = false;

  constructor(
    private router: Router,
    public authService: AuthService
  ) {

    this.isAdmin =
      localStorage.getItem('role')
      === 'ADMIN';
  }

  logout(): void {

    localStorage.removeItem('token');

    localStorage.removeItem('role');

    localStorage.removeItem('firstName');

    this.router.navigate(
      ['/login']
    );

  }
}