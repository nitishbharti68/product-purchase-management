import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import {
  Router
} from '@angular/router';

import {
  AuthService
} from '../../../core/services/auth.service';

import {
  RegisterRequest
} from '../../../core/models/register-request';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule
  ],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class Register {

  registerForm: FormGroup;

  errorMessage = '';

  successMessage = '';

  submitting = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {

    this.registerForm = this.fb.group({

      firstName: ['', Validators.required],

      lastName: ['', Validators.required],

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
      ],

      confirmPassword: [
        '',
        Validators.required
      ]

    });

  }

  onSubmit(): void {

    if (this.registerForm.invalid) {

      this.registerForm.markAllAsTouched();

      return;
    }

    const request: RegisterRequest =
      this.registerForm.value;

    if (
      request.password
      !==
      request.confirmPassword
    ) {

      this.errorMessage =
        'Passwords do not match';

      return;
    }

    this.submitting = true;

    this.errorMessage = '';

    this.authService
      .register(request)
      .subscribe({

        next: () => {

          this.submitting = false;

          this.successMessage =
            'Registration successful. '
            + 'Redirecting to login...';

          setTimeout(
            () => {
              this.router.navigate(
                ['/login']
              );
            },
            1500
          );

        },

        error: error => {

          console.error(error);

          this.submitting = false;

          if (
            typeof error.error === 'string'
          ) {

            this.errorMessage =
              error.error;

          } else if (error.error?.password) {

            this.errorMessage =
              error.error.password;

          } else if (error.error?.email) {

            this.errorMessage =
              error.error.email;

          } else {

            this.errorMessage =
              'Registration failed. '
              + 'Please try again.';

          }

        }

      });

  }

}