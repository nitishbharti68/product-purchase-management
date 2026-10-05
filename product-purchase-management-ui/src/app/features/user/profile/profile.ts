import {
  Component,
  OnInit,
  ChangeDetectorRef
} from '@angular/core';

import {
  CommonModule
} from '@angular/common';

import {
  RouterLink
} from '@angular/router';

import {
  Navbar
} from '../../../shared/navbar/navbar';

import {
  UserResponse
} from '../../../core/models/user-response';

import {
  UserService
} from '../../../core/services/user.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    Navbar
  ],
  templateUrl: './profile.html',
  styleUrl: './profile.css'
})
export class Profile implements OnInit {

  user: UserResponse | null = null;

  loading = false;

  errorMessage = '';

  constructor(
    private userService: UserService,
    private changeDetectorRef: ChangeDetectorRef
  ) {
  }

  ngOnInit(): void {

    this.loadProfile();

  }

  loadProfile(): void {

    this.loading = true;

    this.errorMessage = '';

    this.userService
      .getCurrentUser()
      .subscribe({

        next: response => {

          this.user = response;

          this.loading = false;

          this.changeDetectorRef
            .detectChanges();

        },

        error: error => {

          console.error(error);

          this.errorMessage =
            'Unable to load profile.';

          this.loading = false;

          this.changeDetectorRef
            .detectChanges();

        }

      });

  }

  getInitials(): string {

    if (!this.user) {
      return '';
    }

    const first =
      this.user.firstName
        .charAt(0)
        .toUpperCase();

    const last =
      this.user.lastName
        .charAt(0)
        .toUpperCase();

    return first + last;

  }

  getRoleLabel(): string {

    if (!this.user) {
      return '';
    }

    return this.user.role === 'ADMIN'
      ? 'Administrator'
      : 'End User';

  }

}