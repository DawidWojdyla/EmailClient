package it.dawidwojdyla.controller;

/**
 * Created by Dawid on 2020-12-01.
 */
public enum EmailLoginResult {
    SUCCESS,
    FAILED_BY_CREDENTIALS,
    FAILDE_BY_NETWORK,
    FAILED_BY_UNEXPECTED_ERROR;
}
