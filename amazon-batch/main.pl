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

# ItemIds: up to 10 ASINs
# https://webservices.amazon.com/paapi5/documentation/get-items.html
if (@ARGV == 0) {
    die "Usage: $0 ASIN(s...)\n";
}

my @asins = @ARGV;

print STDERR "# Getting information for @asins\n";

my $payload = Amazon::PAApi5::Payload->new(
    'tokuhirom-22',
    'www.amazon.co.jp',
)->to_json({
    ItemIds     => [@asins],
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

__END__

=head1 NAME

main.pl - Get information from Amazon API

=head1 SYNOPSIS

    AMAZON_ACCESS_KEY=... AMAZON_SECRET_KEY=... perl main.pl B01M2BOZDL B0DNVYNZ2M

=head1 DESCRIPTION

This script gets information from Amazon API.

=head1 ENVIRONMENT VARIABLES

=over 4

=item AMAZON_ACCESS_KEY

Amazon access key

=item AMAZON_SECRET_KEY

Amazon secret key

=back

=head1 Sample output

    {
    "ItemsResult" : {
        "Items" : [
            {
                "ASIN" : "B01M2BOZDL",
                "DetailPageURL" : "https://www.amazon.co.jp/dp/B01M2BOZDL?tag=tokuhirom-22&linkCode=ogi&th=1&psc=1",
                "Images" : {
                "Primary" : {
                    "Medium" : {
                        "Height" : 160,
                        "URL" : "https://m.media-amazon.com/images/I/41qbDGU0U9S._SL160_.jpg",
                        "Width" : 160
                    }
                }
                },
                "ItemInfo" : {
                "Title" : {
                    "DisplayValue" : "ãã¬ãª çä¸æã£ãã¼ãã©ã¼ã¯ 5æ¬+1æ¬ 18.5cm 18-0ã¹ãã³ã¬ã¹ æ¥æ¬è£½",
                    "Label" : "Title",
                    "Locale" : "ja_JP"
                }
                }
            },
            {
                "ASIN" : "B0DNVYNZ2M",
                "DetailPageURL" : "https://www.amazon.co.jp/dp/B0DNVYNZ2M?tag=tokuhirom-22&linkCode=ogi&th=1&psc=1",
                "Images" : {
                "Primary" : {
                    "Medium" : {
                        "Height" : 160,
                        "URL" : "https://m.media-amazon.com/images/I/21WIu7vmYiL._SL160_.jpg",
                        "Width" : 160
                    }
                }
                },
                "ItemInfo" : {
                "Title" : {
                    "DisplayValue" : "BOOX Palma2 6.13ã¤ã³ã éå­ã¼ãã¼ã¿ãã¬ãã Android13 æç´èªè¨¼ä»¢ããã°ã¬ã¼ã8ã³ã¢CPU RAM6GB ROM128GB (ãã©ãã¯)",
                    "Label" : "Title",
                    "Locale" : "ja_JP"
                }
                }
            }
        ]
    }
    }

=cut
