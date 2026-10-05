import {
  Component
} from '@angular/core';

import {
  CommonModule
} from '@angular/common';

import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import {
  Router,
  RouterLink
} from '@angular/router';

import {
  AuthService
} from '../../../core/services/auth.service';

import {
  LoginRequest
} from '../../../core/models/login-request';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {

  loginForm: FormGroup;

  errorMessage = '';

  submitting = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {

    this.loginForm = this.fb.group({

      email: [
        '',
        [
          Validators.required,
          Validators.email
        ]
      ],

      password: [
        '',
        Validators.required
      ]

    });

  }

  onSubmit(): void {

    if (this.loginForm.invalid) {

      this.loginForm.markAllAsTouched();

      return;
    }

    this.submitting = true;

    this.errorMessage = '';

    const request: LoginRequest = {

      email:
        this.loginForm.value.email,

      password:
        this.loginForm.value.password

    };

    this.authService
      .login(request)
      .subscribe({

        next: response => {

          localStorage.setItem(
            'token',
            response.token
          );

          localStorage.setItem(
            'role',
            response.role
          );

          localStorage.setItem(
            'firstName',
            response.firstName
          );

          this.submitting = false;

          this.router.navigate(
            ['/home']
          );

        },

        error: error => {

          console.error(error);

          this.submitting = false;

          if (error.status === 401) {

            this.errorMessage =
              'Invalid email or password';

          } else if (error.status === 0) {

            this.errorMessage =
              'Unable to connect to the server';

          } else {

            this.errorMessage =
              'Login failed. Please try again.';

          }

        }

      });

  }

}