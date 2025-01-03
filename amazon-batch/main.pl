#!/usr/bin/env perl
use strict;
use warnings;
use FindBin;
use lib $FindBin::Bin.'/extlib/lib/perl5';

use Amazon::PAApi5::Payload;
use Amazon::PAApi5::Signature;
use HTTP::Request::Common;
use LWP::UserAgent;
use Data::Dumper;

my $access_key = $ENV{AMAZON_ACCESS_KEY} // die "Missing amazon access key";
my $secret_key = $ENV{AMAZON_SECRET_KEY} // die "Missing amazon secret key";

my $asin = shift @ARGV // die "Missing ASIN";

print STDERR "# Getting information for $asin\n";

my $payload = Amazon::PAApi5::Payload->new(
    'tokuhirom-22',
    'www.amazon.co.jp',
)->to_json({
    ItemIds     => [$asin],
    Resources   => [qw/
        ItemInfo.Title Images.Primary.Medium
    /],
});
my $sig = Amazon::PAApi5::Signature->new(
    $access_key,
    $secret_key,
    $payload,
    {
        resource_path => '/paapi5/getitems',
        operation     => 'GetItems',
        host          => 'webservices.amazon.co.jp',
        region        => 'us-west-2',
    }
);

my $ua = LWP::UserAgent->new;
my $req = POST $sig->req_url, $sig->headers, Content => $sig->payload;
my $res = $ua->request($req);

if ($res->is_success) {
    print($res->content);
} else {
    die "Cannot fetch the data from Amazon: @{[ $res->status_line ]}, @{[ $res->content ]}";
}
