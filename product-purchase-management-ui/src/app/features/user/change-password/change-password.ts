import {
  Component
} from '@angular/core';

import {
  CommonModule
} from '@angular/common';

import {
  ReactiveFormsModule,
  FormBuilder,
  Validators,
  FormGroup
} from '@angular/forms';

import {
  Navbar
} from '../../../shared/navbar/navbar';

import {
  UserService
} from '../../../core/services/user.service';

import {
  ChangePasswordRequest
} from '../../../core/models/change-password-request';

@Component({
  selector: 'app-change-password',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    Navbar
  ],
  templateUrl: './change-password.html',
  styleUrl: './change-password.css'
})
export class ChangePassword {

  passwordForm: FormGroup;

  successMessage = '';

  errorMessage = '';

  submitting = false;

  constructor(
    private fb: FormBuilder,
    private userService: UserService
  ) {

    this.passwordForm =
      this.fb.group({

        currentPassword: [
          '',
          Validators.required
        ],

        newPassword: [
          '',
          Validators.required
        ],

        confirmNewPassword: [
          '',
          Validators.required
        ]

      });

  }

  changePassword(): void {

    if (
      this.passwordForm.invalid
    ) {
      return;
    }

    const request:
      ChangePasswordRequest =
      this.passwordForm.getRawValue();

    if (
      request.newPassword
      !==
      request.confirmNewPassword
    ) {

      this.showError(
        'New password and confirm '
        + 'new password do not match'
      );

      return;
    }

    this.submitting = true;

    this.userService
      .changePassword(request)
      .subscribe({

        next: () => {

          this.submitting = false;

          this.showSuccess(
            'Password changed successfully'
          );

          this.passwordForm.reset();

        },

        error: error => {

          console.error(error);

          this.submitting = false;

          this.showError(
            this.extractError(
              error,
              'Unable to change password'
            )
          );

        }

      });

  }

  clearMessages(): void {

    this.successMessage = '';

    this.errorMessage = '';

  }

  private showSuccess(
    message: string
  ): void {

    this.successMessage = message;

    this.errorMessage = '';

    setTimeout(
      () => {
        this.successMessage = '';
      },
      4000
    );

  }

  private showError(
    message: string
  ): void {

    this.errorMessage = message;

    this.successMessage = '';

  }

  private extractError(
    error: any,
    fallback: string
  ): string {

    if (
      typeof error.error === 'string'
    ) {
      return error.error;
    }

    if (error.error?.message) {
      return error.error.message;
    }

    if (error.error?.newPassword) {
      return error.error.newPassword;
    }

    if (error.error?.currentPassword) {
      return error.error.currentPassword;
    }

    if (error.error?.errors) {

      const firstKey =
        Object.keys(
          error.error.errors
        )[0];

      if (firstKey) {
        return error.error.errors[firstKey];
      }

    }

    return fallback;

  }

}